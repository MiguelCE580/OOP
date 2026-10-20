package edureach.data;

import edureach.model.*;
import edureach.util.TimeUtil;

import java.util.List;

/**
 * Demo dataset written to the single JSON file on first run.
 * Mirrors the sample data script that will ship with the MySQL DDL
 * in Phase 4 (docs/schema.sql TODO).
 */
public final class SeedData {

    private SeedData() {}

    public static DB build() {
        DB db = new DB();
        db.config.setTheme("dark");          // dark mode is the shipped default
        db.config.setFontScale(1);
        db.config.setLang("EN");

        // ---------- users ----------
        add(db, UserFactory.createUser(Role.STUDENT, "u1", "maria.santos", pw("learn123"),
                "Maria Santos", "maria.santos@edureach.ph"));
        add(db, UserFactory.createUser(Role.STUDENT, "u2", "jose.ramos", pw("learn123"),
                "Jose Ramos", "jose.ramos@edureach.ph"));
        add(db, UserFactory.createUser(Role.STUDENT, "u3", "ana.cruz", pw("learn123"),
                "Ana Dela Cruz", "ana.cruz@edureach.ph"));
        add(db, UserFactory.createUser(Role.STUDENT, "u4", "mark.villanueva", pw("learn123"),
                "Mark Villanueva", "mark.villanueva@edureach.ph"));
        add(db, UserFactory.createUser(Role.STUDENT, "u5", "liza.bautista", pw("learn123"),
                "Liza Bautista", "liza.bautista@edureach.ph"));
        add(db, UserFactory.createUser(Role.INSTRUCTOR, "i1", "ms.reyes", pw("teach123"),
                "Ms. Reyes", "m.reyes@edureach.ph"));
        add(db, UserFactory.createUser(Role.ADMIN, "a1", "admin", pw("admin123"),
                "Center Admin", "admin@edureach.ph"));

        ((StudentUser) db.users.get(3)).setExtendedTime(true);      // Mark: extended-time accommodation
        ((StudentUser) db.users.get(0)).setGradeLevel("Grade 7");
        ((StudentUser) db.users.get(4)).setGradeLevel("Grade 5");
        ((StudentUser) db.users.get(3)).setGradeLevel("Grade 8");
        ((InstructorUser) db.users.get(5)).setSpecialization("IT / Digital Literacy");

        // ---------- skill domains ----------
        db.domains.add(new SkillDomain("d1", "Basic Computer Use", "Keyboard Pro"));
        db.domains.add(new SkillDomain("d2", "Internet Safety", "Safe Surfer"));
        db.domains.add(new SkillDomain("d3", "Financial Basics", "Budget Wise"));
        db.domains.add(new SkillDomain("d4", "Files & Organization", "File Master"));
        db.domains.add(new SkillDomain("d5", "Math Foundations", "Math Whiz"));

        // ---------- courses & lessons ----------
        db.courses.add(new Course("c1", "Basic Computer Use", "IT", "i1",
                "Hardware, input devices and file management for first-time computer users."));
        db.courses.add(new Course("c2", "Internet Safety", "IT", "i1",
                "Passwords, phishing and safe browsing for community learners."));
        db.courses.add(new Course("c3", "Financial Basics", "Livelihood", "i1",
                "Budgeting, needs vs wants and mobile money essentials."));

        db.lessons.add(new Lesson("l1", "c1", "Parts of a computer", "Slides", 1,
                "The CPU is the brain of the computer. The monitor shows your work (output), "
              + "the keyboard and mouse send instructions (input), and storage keeps your files "
              + "even when the machine is off.", "d1"));
        db.lessons.add(new Lesson("l2", "c1", "Using the keyboard and mouse", "Video", 2,
                "Practice clicking, double-clicking and dragging. Learn the home row and the "
              + "difference between Enter, Backspace and Tab.", "d1"));
        db.lessons.add(new Lesson("l3", "c1", "Files and folders", "PDF", 3,
                "A folder stores files. Ctrl+C copies, Ctrl+X cuts, Ctrl+V pastes, Ctrl+S saves. "
              + "File extensions like .pdf, .docx and .png tell you what a file is.", "d4"));
        db.lessons.add(new Lesson("l4", "c1", "Typing a document", "Slides", 4,
                "Open a word processor, set a font size you can read, type a paragraph and save "
              + "it inside your own folder.", "d1"));
        db.lessons.add(new Lesson("l5", "c1", "Email basics", "Video", 5,
                "Create an address, write a subject line, attach a file and send politely.", "d2"));

        db.lessons.add(new Lesson("l6", "c2", "Passwords that stick", "Slides", 1,
                "Long passphrases beat short clever passwords. Never reuse your login password.", "d2"));
        db.lessons.add(new Lesson("l7", "c2", "Spotting phishing", "Video", 2,
                "Check the sender, hover links, and be suspicious of urgency and prizes.", "d2"));
        db.lessons.add(new Lesson("l8", "c2", "Safe browsing habits", "PDF", 3,
                "Look for https, keep software updated, and log out on shared computers.", "d2"));
        db.lessons.add(new Lesson("l9", "c2", "Your digital footprint", "Activity", 4,
                "Everything posted online can be copied. Think before you share.", "d2"));

        db.lessons.add(new Lesson("l10", "c3", "Needs vs wants", "Slides", 1,
                "Needs keep you alive and working; wants improve comfort. Sort 10 examples.", "d3"));
        db.lessons.add(new Lesson("l11", "c3", "Simple budgeting", "PDF", 2,
                "Income minus needs minus savings leaves wants. Write your own weekly budget.", "d3"));
        db.lessons.add(new Lesson("l12", "c3", "Mobile money basics", "Video", 3,
                "Protect your PIN, verify recipient numbers, and keep transaction receipts.", "d3"));

        // ---------- quizzes & questions ----------
        quiz(db, "q1", "c1", "Quiz 1: Computer Parts & Input", 10, 70, false, "");
        question(db, "q1", "d1", "Which part of the computer is considered its \"brain\"?",
                new String[]{"Monitor", "CPU", "Mouse", "Speaker"}, 1);
        question(db, "q1", "d1", "Which device is used to type instructions into the computer?",
                new String[]{"Printer", "Monitor", "Keyboard", "Projector"}, 2);
        question(db, "q1", "d1", "Where is your work kept after the computer is switched off?",
                new String[]{"RAM", "The monitor", "The cache", "Storage (hard drive / SSD)"}, 3);
        question(db, "q1", "d1", "What does GPU stand for?",
                new String[]{"Graphics Processing Unit", "General Power Utility",
                             "Game Program Updater", "Graph Printing Unit"}, 0);

        quiz(db, "q2", "c1", "Quiz 2: Files and Folders", 15, 70, false, "");
        question(db, "q2", "d4", "Which key combination copies the selected file?",
                new String[]{"Ctrl + V", "Ctrl + C", "Ctrl + X", "Ctrl + Z"}, 1);
        question(db, "q2", "d4", "Where do deleted files go first?",
                new String[]{"Recycle Bin", "Desktop", "Documents", "Control Panel"}, 0);
        question(db, "q2", "d4", "Which extension identifies a PDF document?",
                new String[]{".docx", ".png", ".pdf", ".exe"}, 2);
        question(db, "q2", "d4", "Which shortcut pastes what you copied?",
                new String[]{"Ctrl + P", "Ctrl + S", "Ctrl + V", "Ctrl + C"}, 2);

        quiz(db, "q3", "c2", "Quiz 1: Staying Safe Online", 10, 70, false, "");
        question(db, "q3", "d2", "Which password is the strongest?",
                new String[]{"123456", "password", "maria1990", "blue-tiger-eats-rice-42"}, 3);
        question(db, "q3", "d2", "A message says you won a prize and must click now. What is it?",
                new String[]{"Likely phishing", "Good news", "A system update", "An advertisement only"}, 0);
        question(db, "q3", "d2", "Which address shows a secure website?",
                new String[]{"http://shop.ph", "https://bank.ph", "ftp://bank.ph", "file://bank.ph"}, 1);
        question(db, "q3", "d2", "On a shared computer you should always...",
                new String[]{"Save passwords in the browser", "Leave tabs open",
                             "Log out when done", "Disable the screen lock"}, 2);

        quiz(db, "q4", "c3", "Quiz 1: Money Basics", 10, 70, false, "");
        question(db, "q4", "d3", "Which of these is a NEED?",
                new String[]{"Movie tickets", "Rent and rice", "New sneakers", "Load for games"}, 1);
        question(db, "q4", "d3", "A budget mainly helps you to...",
                new String[]{"Spend everything", "Plan income vs expenses",
                             "Avoid working", "Borrow more"}, 1);
        question(db, "q4", "d3", "Money set aside for emergencies is called...",
                new String[]{"Debt", "Savings", "Interest", "Allowance"}, 1);
        question(db, "q4", "d3", "Your mobile money PIN should be...",
                new String[]{"Shared with friends", "Written on your phone case",
                             "Kept private", "Your birthday"}, 2);

        // Simplified retake used by the intervention module.
        quiz(db, "q5", "c1", "Remedial Quiz: Computer Basics Essentials", 20, 60, true, "q1");
        question(db, "q5", "d1", "Which part shows you your work on screen?",
                new String[]{"Monitor", "Keyboard", "Mouse", "Printer"}, 0);
        question(db, "q5", "d1", "Which device do you move with your hand to point?",
                new String[]{"Mouse", "Speaker", "Monitor", "Scanner"}, 0);
        question(db, "q5", "d4", "Where do you keep your files organized?",
                new String[]{"Folders", "The desktop background", "The power button", "The Recycle Bin"}, 0);
        question(db, "q5", "d4", "Which keys save your document?",
                new String[]{"Ctrl + S", "Ctrl + Q", "Alt + F4", "Esc"}, 0);

        // ---------- enrollments ----------
        enroll(db, "e1", "u1", "c1", "2026-09-14", List.of("l1", "l2", "l3", "l4", "l5"));
        enroll(db, "e2", "u2", "c1", "2026-09-14", List.of("l1", "l2", "l3"));
        enroll(db, "e3", "u3", "c1", "2026-09-14", List.of("l1", "l2", "l3", "l4", "l5"));
        enroll(db, "e4", "u4", "c1", "2026-09-15", List.of("l1"));
        enroll(db, "e5", "u5", "c1", "2026-09-15", List.of("l1", "l2", "l3", "l4"));
        enroll(db, "e6", "u1", "c2", "2026-09-21", List.of("l6", "l7", "l8", "l9"));
        enroll(db, "e7", "u2", "c2", "2026-09-21", List.of("l6", "l7"));
        enroll(db, "e8", "u3", "c2", "2026-09-21", List.of("l6", "l7", "l8"));
        enroll(db, "e9", "u1", "c3", "2026-09-28", List.of("l10", "l11"));
        enroll(db, "e10", "u5", "c3", "2026-09-28", List.of("l10", "l11", "l12"));

        // ---------- practical scores (/10) ----------
        db.practicalScores.add(new PracticalScore("p1", "u1", "c1", 9, "i1"));
        db.practicalScores.add(new PracticalScore("p2", "u2", "c1", 7, "i1"));
        db.practicalScores.add(new PracticalScore("p3", "u3", "c1", 10, "i1"));
        db.practicalScores.add(new PracticalScore("p4", "u4", "c1", 5, "i1"));
        db.practicalScores.add(new PracticalScore("p5", "u5", "c1", 8, "i1"));

        // ---------- submissions (answer = option index per question in order) ----------
        // q1: CPU / Keyboard / Storage / GPU
        attempt(db, "u1", "q1", new int[]{1, 2, 3, 0}, "7:40", "2026-09-28 09:10");   // 100
        attempt(db, "u2", "q1", new int[]{1, 2, 0, 1}, "9:05", "2026-09-28 09:12");   // 50
        attempt(db, "u3", "q1", new int[]{1, 2, 3, 0}, "6:55", "2026-09-28 09:15");   // 100
        attempt(db, "u4", "q1", new int[]{0, 2, 0, 2}, "9:50", "2026-09-28 09:18");   // 25
        attempt(db, "u5", "q1", new int[]{1, 2, 3, 2}, "8:30", "2026-09-28 09:20");   // 75
        // q2: Ctrl+C / Recycle / .pdf / Ctrl+V   (.pdf item is the class-wide hard item)
        attempt(db, "u1", "q2", new int[]{1, 0, 0, 2}, "11:20", "2026-10-05 09:05");  // 75
        attempt(db, "u2", "q2", new int[]{1, 1, 0, 2}, "13:40", "2026-10-05 09:08");  // 50
        attempt(db, "u3", "q2", new int[]{1, 0, 2, 2}, "10:10", "2026-10-05 09:11");  // 100
        attempt(db, "u4", "q2", new int[]{0, 3, 0, 1}, "14:25", "2026-10-05 09:14");  // 25
        attempt(db, "u5", "q2", new int[]{1, 0, 0, 2}, "12:00", "2026-10-05 09:17");  // 75
        // q3
        attempt(db, "u1", "q3", new int[]{3, 0, 1, 2}, "6:40", "2026-10-12 10:02");   // 100
        attempt(db, "u2", "q3", new int[]{3, 0, 0, 2}, "8:15", "2026-10-12 10:05");   // 75
        attempt(db, "u3", "q3", new int[]{2, 0, 1, 2}, "7:30", "2026-10-12 10:08");   // 75
        // q4
        attempt(db, "u1", "q4", new int[]{1, 1, 1, 2}, "5:50", "2026-10-15 13:01");   // 100
        attempt(db, "u5", "q4", new int[]{1, 0, 1, 2}, "7:20", "2026-10-15 13:04");   // 75

        // ---------- attendance ----------
        String[] dates = {"2026-10-05", "2026-10-08", "2026-10-12", "2026-10-15"};
        String[][] marks = {
                {"P", "P", "P", "P"},   // e1 maria
                {"P", "A", "P", "P"},   // e2 jose
                {"P", "P", "P", "P"},   // e3 ana
                {"P", "A", "A", "L"},   // e4 mark
                {"L", "P", "P", "P"},   // e5 liza
        };
        String[] enr = {"e1", "e2", "e3", "e4", "e5"};
        int n = 0;
        for (int i = 0; i < enr.length; i++) {
            for (int d = 0; d < dates.length; d++) {
                db.attendance.add(new AttendanceRecord("att-" + (++n), enr[i], dates[d], marks[i][d]));
            }
        }

        // ---------- certificate ----------
        db.certificates.add(new Certificate("cert-1", "u3", "c1", "2026-10-03",
                "ER-2026-7A4Q-0100", 96.7));

        // ---------- curriculum (Mathematics flagship, Elem -> HS) ----------
        topic(db, "t01", CurriculumTrack.ELEMENTARY, "Mathematics", "Numbers and counting",
                "Count, read and write numbers up to 1,000; place value of ones, tens, hundreds.", 40);
        topic(db, "t02", CurriculumTrack.ELEMENTARY, "Mathematics", "Addition and subtraction facts",
                "Memorize basic facts up to 20 and solve one-step problems with regrouping.", 45);
        topic(db, "t03", CurriculumTrack.ELEMENTARY, "Mathematics", "Multiplication and division",
                "Times tables up to 10, long multiplication basics, division with remainders.", 50);
        topic(db, "t04", CurriculumTrack.ELEMENTARY, "Mathematics", "Fractions basics",
                "Part of a whole, equivalent fractions, comparing and simple addition of fractions.", 45);
        topic(db, "t05", CurriculumTrack.ELEMENTARY, "Mathematics", "Decimals and money",
                "Reading decimals, converting pesos and centavos, adding prices.", 40);
        topic(db, "t06", CurriculumTrack.ELEMENTARY, "Mathematics", "Shapes and measurement",
                "Perimeter and area of squares and rectangles; telling time; lengths in cm and m.", 45);
        topic(db, "t07", CurriculumTrack.ELEMENTARY, "Mathematics", "Word problem strategies",
                "Read, draw, plan, solve, check - the 5-step routine for story problems.", 40);
        topic(db, "t08", CurriculumTrack.ELEMENTARY, "Science", "Living and non-living things",
                "Sort objects, describe needs of living things, observe the school garden.", 35);
        topic(db, "t09", CurriculumTrack.ELEMENTARY, "English", "Reading comprehension basics",
                "Main idea, details, and retelling a short paragraph in your own words.", 35);

        topic(db, "t10", CurriculumTrack.JUNIOR_HIGH, "Mathematics", "Integers and operations",
                "Negative numbers, order of operations (PESADM), number line reasoning.", 45);
        topic(db, "t11", CurriculumTrack.JUNIOR_HIGH, "Mathematics", "Introduction to algebra",
                "Variables, expressions, translating sentences into algebra.", 50);
        topic(db, "t12", CurriculumTrack.JUNIOR_HIGH, "Mathematics", "Linear equations",
                "Solving one- and two-step equations, checking solutions, word problems.", 55);
        topic(db, "t13", CurriculumTrack.JUNIOR_HIGH, "Mathematics", "Ratio and proportion",
                "Ratios, rates, unit pricing at the sari-sari store, direct proportion.", 45);
        topic(db, "t14", CurriculumTrack.JUNIOR_HIGH, "Mathematics", "Geometry and angles",
                "Angle types, triangle sum, area and volume of common solids.", 50);
        topic(db, "t15", CurriculumTrack.JUNIOR_HIGH, "Mathematics", "Statistics basics",
                "Mean, median, mode; reading bar and line graphs of class data.", 40);
        topic(db, "t16", CurriculumTrack.JUNIOR_HIGH, "Science", "The scientific method",
                "Question, hypothesis, experiment, conclusion - with a simple plant trial.", 40);
        topic(db, "t17", CurriculumTrack.JUNIOR_HIGH, "English", "Essay writing",
                "Topic sentence, support, conclusion; peer editing checklist.", 45);

        topic(db, "t18", CurriculumTrack.SENIOR_HIGH, "Mathematics", "Functions and graphs",
                "Domain and range, linear vs quadratic graphs, interpreting intercepts.", 60);
        topic(db, "t19", CurriculumTrack.SENIOR_HIGH, "Mathematics", "Trigonometry basics",
                "Right-triangle ratios, sine/cosine/tangent, angle of elevation problems.", 60);
        topic(db, "t20", CurriculumTrack.SENIOR_HIGH, "Mathematics", "Probability and statistics",
                "Counting principles, basic probability, normal curve intuition.", 55);
        topic(db, "t21", CurriculumTrack.SENIOR_HIGH, "Mathematics", "Business math and interest",
                "Simple and compound interest, discounts, mark-up, installment plans.", 50);
        topic(db, "t22", CurriculumTrack.SENIOR_HIGH, "Mathematics", "Calculus concepts",
                "Limits intuitively, rate of change, area under a curve.", 60);
        topic(db, "t23", CurriculumTrack.SENIOR_HIGH, "Science", "Research basics",
                "Variables, sampling, and writing a simple research abstract.", 50);
        topic(db, "t24", CurriculumTrack.SENIOR_HIGH, "English", "Academic writing",
                "Thesis statements, citations, and avoiding plagiarism.", 45);

        db.curriculumFollows.add(new CurriculumFollow("u5", CurriculumTrack.ELEMENTARY));
        db.curriculumFollows.get(0).getStudiedTopics().addAll(List.of("t01", "t02", "t03"));
        db.curriculumFollows.add(new CurriculumFollow("u4", CurriculumTrack.JUNIOR_HIGH));

        // ---------- audit trail ----------
        db.auditLogs.add(new AuditEntry("log-1", "u1", "LOGIN_SUCCESS (Student)", "2026-10-19 08:02"));
        db.auditLogs.add(new AuditEntry("log-2", "i1", "LOGIN_SUCCESS (Instructor)", "2026-10-19 08:15"));
        db.auditLogs.add(new AuditEntry("log-3", "i1", "COURSE_UPDATED course=c1", "2026-10-19 08:20"));
        db.auditLogs.add(new AuditEntry("log-4", "u3", "CERTIFICATE_ISSUED code=ER-2026-7A4Q-0100", "2026-10-03 16:44"));
        db.auditLogs.add(new AuditEntry("log-5", "a1", "DATABASE_BACKUP created", "2026-10-18 18:00"));

        return db;
    }

    // ---------------- helpers ----------------

    private static String pw(String plain) { return TimeUtil.hashPassword(plain); }

    private static void add(DB db, User u) { db.users.add(u); }

    private static void quiz(DB db, String id, String courseId, String title,
                             int minutes, int passing, boolean remedial, String forQuiz) {
        Quiz q = new Quiz(id, courseId, title, minutes, passing);
        q.setRemedial(remedial);
        q.setForQuizId(forQuiz);
        db.quizzes.add(q);
    }

    private static int qSeq = 0;
    private static int oSeq = 0;

    private static void question(DB db, String quizId, String domainId, String text,
                                 String[] options, int correctIndex) {
        String qid = "ques-" + (++qSeq);
        db.questions.add(new Question(qid, quizId, domainId, text));
        for (int i = 0; i < options.length; i++) {
            db.options.add(new QuestionOption("opt-" + (++oSeq), qid, options[i], i == correctIndex));
        }
    }

    private static void enroll(DB db, String id, String studentId, String courseId,
                               String on, List<String> completed) {
        Enrollment e = new Enrollment(id, studentId, courseId, on);
        e.getCompletedLessons().addAll(completed);
        db.enrollments.add(e);
    }

    /** Builds a submission by choosing option indexes per question (in stored order). */
    private static int subSeq = 0;

    private static void attempt(DB db, String studentId, String quizId, int[] picks,
                                String timeUsed, String at) {
        List<Question> qs = db.questions.stream().filter(q -> q.getQuizId().equals(quizId)).toList();
        String subId = "sub-" + (++subSeq);
        int correct = 0;
        for (int i = 0; i < qs.size(); i++) {
            Question q = qs.get(i);
            List<QuestionOption> opts = db.options.stream()
                    .filter(o -> o.getQuestionId().equals(q.getQuestionId())).toList();
            QuestionOption chosen = opts.get(picks[i]);
            if (chosen.isCorrect()) correct++;
            db.submissionAnswers.add(new SubmissionAnswer(subId, q.getQuestionId(),
                    chosen.getOptionId(), chosen.isCorrect()));
        }
        double pct = Math.round(correct * 10000.0 / qs.size()) / 100.0;
        db.submissions.add(new Submission(subId, studentId, quizId, pct, correct,
                qs.size(), timeUsed, at));
    }

    private static void topic(DB db, String id, CurriculumTrack track, String subject,
                              String title, String desc, int minutes) {
        db.curriculumTopics.add(new CurriculumTopic(id, track, subject, title, desc, minutes));
    }
}
