import com.marssvn.svnapi.ISvnAdmin;
import com.marssvn.svnapi.ISvnClient;
import com.marssvn.svnapi.SvnAdminForLocale;
import com.marssvn.svnapi.SvnClient;
import com.marssvn.svnapi.exception.SvnApiException;
import com.marssvn.svnapi.model.SvnUser;
import org.junit.*;
import org.junit.runners.MethodSorters;

/**
 * SVNClientImpl Tester.
 *
 * @author zhangkx
 * @version 1.0
 * @since 2018-10-31
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SVNClientTest {

    private final static String REPOSITORY_NAME = "marssvn";

    @BeforeClass
    public static void before() throws Exception {
        ISvnAdmin svnAdmin = new SvnAdminForLocale();
        svnAdmin.deleteRepository(REPOSITORY_NAME);
        svnAdmin.createRepository(REPOSITORY_NAME);
    }

    @AfterClass
    public static void after() throws Exception {
//        ISvnAdmin svnAdmin = new SvnAdminForLocale();
//        svnAdmin.deleteRepository(REPOSITORY_NAME);
    }

    /**
     * mkdir, success
     */
    @Test
    public void test01_MakeDir() {
        SvnUser svnUser = new SvnUser("marssvn", "marssvn");
        ISvnClient svnClient = new SvnClient();
        svnClient.setRootPath("svn://localhost/" + REPOSITORY_NAME);
        svnClient.setSvnUser(svnUser);
        svnClient.mkdir("111/222/333", "test make dir 111/222/333");
        svnClient.mkdir("demo", "test make dir demo");
        svnClient.mkdir("src", "test make dir src");
    }

    /**
     * mkdir, throw SvnApiException when SvnUser is null
     */
    @Test(expected = SvnApiException.class)
    public void test02_MakeDir() {
        ISvnClient svnClient = new SvnClient();
        svnClient.setRootPath("svn://localhost/" + REPOSITORY_NAME);
        svnClient.mkdir("demo2", "test make dir demo");
    }

    /**
     * mkdir, throw SvnApiException when root path is null
     */
    @Test(expected = SvnApiException.class)
    public void test03_MakeDir() {
        SvnUser svnUser = new SvnUser("marssvn", "marssvn");
        ISvnClient svnClient = new SvnClient();
        svnClient.setSvnUser(svnUser);
        svnClient.mkdir("demo3", "test make dir demo");
    }

    @Test
    public void test04_HeadRevision() {
        SvnUser svnUser = new SvnUser("marssvn", "marssvn");
        ISvnClient svnClient = new SvnClient();
        svnClient.setRootPath("svn://localhost/" + REPOSITORY_NAME);
        svnClient.setSvnUser(svnUser);
        long headRevision = svnClient.headRevision("demo");
        Assert.assertEquals(3, headRevision);
    }

    @Test
    public void test05_LastChangedRevision() {
        SvnUser svnUser = new SvnUser("marssvn", "marssvn");
        ISvnClient svnClient = new SvnClient();
        svnClient.setRootPath("svn://localhost/" + REPOSITORY_NAME);
        svnClient.setSvnUser(svnUser);
        long headRevision = svnClient.lastChangedRevision("demo");
        Assert.assertEquals(2, headRevision);
    }
}
