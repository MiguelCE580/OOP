package edureach.service;

import edureach.data.DataAccess;
import edureach.data.JsonStore;
import edureach.model.Certificate;
import edureach.model.Course;
import edureach.model.StudentUser;
import edureach.util.IdGen;
import edureach.util.TimeUtil;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Credentials (proposal P4). Phase 2-3: in-app preview + code verification.
 * TODO(Phase 4): real PDF rendering with OpenPDF ("Download PDF" is stubbed).
 */
public final class CertificateService {

    private CertificateService() {}

    public static List<Certificate> ofStudent(String studentId) {
        return JsonStore.get().db().certificates.stream()
                .filter(c -> c.getStudentId().equals(studentId)).toList();
    }

    public static Optional<Certificate> verify(String code) {
        String norm = code.trim().toUpperCase(Locale.ROOT);
        return JsonStore.get().db().certificates.stream()
                .filter(c -> c.getVerificationCode().equalsIgnoreCase(norm))
                .findFirst();
    }

    /** Issues a certificate when the learner's course status becomes CERT_READY. */
    public static void issueIfReady(StudentUser student) {
        for (var e : ProgressService.enrollmentsOf(student.getUserId())) {
            if (!ProgressService.ST_CERT_READY.equals(
                    ProgressService.status(student.getUserId(), e.getCourseId()))) continue;
            boolean exists = JsonStore.get().db().certificates.stream()
                    .anyMatch(c -> c.getStudentId().equals(student.getUserId())
                            && c.getCourseId().equals(e.getCourseId()));
            if (exists) continue;

            Double avg = ProgressService.courseAverage(student.getUserId(), e.getCourseId());
            String code = generateCode();
            JsonStore.get().db().certificates.add(new Certificate(
                    IdGen.next("cert"), student.getUserId(), e.getCourseId(),
                    TimeUtil.today(), code, avg == null ? 0 : avg));
            DataAccess.audit().log(student.getUserId(), "CERTIFICATE_ISSUED code=" + code);
        }
        JsonStore.get().save();
    }

    private static String generateCode() {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        var rnd = new java.security.SecureRandom();
        for (int i = 0; i < 4; i++) sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
        return "ER-2026-" + sb + "-" + String.format("%04d",
                JsonStore.get().db().certificates.size() + 1);
    }

    public static Course courseOf(Certificate cert) {
        return ProgressService.course(cert.getCourseId());
    }
}
