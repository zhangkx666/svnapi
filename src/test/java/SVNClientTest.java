import com.marssvn.svnapi.ISvnClient;
import com.marssvn.svnapi.SvnClient;
import com.marssvn.svnapi.exception.SvnApiException;
import com.marssvn.svnapi.model.SvnEntry;
import com.marssvn.svnapi.model.SvnUser;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.List;

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
    private ISvnClient svnClient;

    @BeforeClass
    public static void beforeClass() throws Exception {
//        ISvnAdmin svnAdmin = new SvnAdminForLocale();
//        svnAdmin.deleteRepository(REPOSITORY_NAME);
//        svnAdmin.createRepository(REPOSITORY_NAME);
    }

    @AfterClass
    public static void afterClass() throws Exception {
//        ISvnAdmin svnAdmin = new SvnAdminForLocale();
//        svnAdmin.deleteRepository(REPOSITORY_NAME);
    }

    @Before
    public void before() throws Exception {
        if (svnClient == null) {
            svnClient = new SvnClient();
            svnClient.setRootPath("svn://localhost/" + REPOSITORY_NAME);
            svnClient.setSvnUser(new SvnUser("marssvn", "marssvn"));
        }
    }

    /**
     * mkdir, success
     */
    @Test
    public void test01_MakeDir() {
        svnClient.mkdir("111/222/333", "test make dir 111/222/333");
        svnClient.mkdir("demo", "test make dir demo");
        svnClient.mkdir("src", "test make dir src");
    }

    /**
     * mkdir, throw SvnApiException when SvnUser is null
     */
    @Test(expected = SvnApiException.class)
    public void test02_MakeDir() {
        ISvnClient svnClient1 = new SvnClient();
        svnClient1.setRootPath("svn://localhost/" + REPOSITORY_NAME);
        svnClient1.mkdir("demo2", "test make dir demo");
    }

    /**
     * mkdir, throw SvnApiException when root path is null
     */
    @Test(expected = SvnApiException.class)
    public void test03_MakeDir() {
        SvnUser svnUser = new SvnUser("marssvn", "marssvn");
        ISvnClient svnClient1 = new SvnClient();
        svnClient1.setSvnUser(svnUser);
        svnClient1.mkdir("demo3", "test make dir demo");
    }

    @Test
    public void test04_HeadRevision() {
        long headRevision = svnClient.headRevision();
        Assert.assertEquals(3, headRevision);
    }

    @Test
    public void test05_LastChangedRevision() {
        long headRevision = svnClient.lastChangedRevision("demo");
        Assert.assertEquals(2, headRevision);
    }

    @Test
    public void test06_list() {
        List<SvnEntry> svnEntryList = svnClient.list("src");
        Assert.assertEquals(svnEntryList.size(), 8);
    }
}
