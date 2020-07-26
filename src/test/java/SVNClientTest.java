import com.marssvn.svnapi.ISvnClient;
import com.marssvn.svnapi.SvnClient;
import com.marssvn.svnapi.exception.SvnApiException;
import com.marssvn.svnapi.model.SvnEntry;
import com.marssvn.svnapi.model.SvnUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
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

    private final static String REPOSITORY_NAME = "test_2019_01";
    private final static String PASSWORD = "_a*6s)FT0qO7|(6X";
    private ISvnClient svnClient;
    private SvnUser svnUser;

//    @BeforeClass
//    public static void beforeClass() throws Exception {
//
//        ISvnAdmin svnAdmin = new SvnAdminForLocale();
//        svnAdmin.deleteRepository(REPOSITORY_NAME);
//
//        SvnRepository svnRepository = new SvnRepository();
//        svnRepository.setName(REPOSITORY_NAME);
//        svnAdmin.createRepository(svnRepository);
//    }

//    @AfterClass
//    public static void afterClass() throws Exception {
//        ISvnAdmin adminUser = new SvnAdminForLocale();
//        adminUser.deleteRepository(REPOSITORY_NAME);
//    }

    @Before
    public void before() throws Exception {
        if (this.svnUser == null) {
            this.svnUser = new SvnUser("marssvn", PASSWORD);
        }
        if (this.svnClient == null) {
            this.svnClient = new SvnClient();
            this.svnClient.setRootPath("svn://localhost/" + REPOSITORY_NAME);
            this.svnClient.setSvnUser(this.svnUser);
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
        ISvnClient svnClient1 = new SvnClient();
        svnClient1.setSvnUser(this.svnUser);
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
