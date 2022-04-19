package at.ac.tuwien.sepm.groupphase.backend.helper;

public class TestData {

    public static Long ADMIN_ID = 2000L;
    public static Long NORMAL_ID = 3000L;

    public static String BASE_URI = "/api/v1";
    public static String SING_IN_URI = BASE_URI + "/signin";
    public static String SIGN_UP_URI = BASE_URI + "/signup";
    public static String UPDATE_URI = BASE_URI + "/user";
    public static String DELETE_URI = BASE_URI + "/user";
    public static String CREATE_URI = BASE_URI + "/user";
    public static String UNLOCK_URI = BASE_URI + "/user/%d/unlock";
    public static String LOCK_URI = BASE_URI + "/user/%d/lock";
    public static String LIST_USER_URI = BASE_URI + "/user";
    public static String NEWS_URI = BASE_URI + "/news";
    public static String PERFORMANCE_URI = BASE_URI + "/performance/";
    public static String TICKET_URI = BASE_URI + "/ticket/";
    public static String EVENT_URI = BASE_URI + "/events/";
    public static String HALL_URI = BASE_URI + "/hall/";

    public static String NOT_EXISTING_EMAIL = "notexisting@email.com";
    public static Long NOT_EXISTING_ID = -100L;

    public static String PLAIN_PASSWORD = "12345678";
}
