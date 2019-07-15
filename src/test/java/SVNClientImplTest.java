import com.marssvn.svnapi.ISvnAdmin;
import com.marssvn.svnapi.ISvnClient;
import com.marssvn.svnapi.SvnAdminForLocale;
import com.marssvn.svnapi.SvnClient;
import com.marssvn.svnapi.model.SvnUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * SVNClientImpl Tester.
 *
 * @author zhangkx
 * @version 1.0
 * @since <pre>2018-10-31</pre>
 */
public class SVNClientImplTest {

    private final static String REPOSITORY_NAME = "marssvn";

    @Before
    public void before() throws Exception {
        ISvnAdmin svnAdmin = new SvnAdminForLocale();
        svnAdmin.deleteRepository(REPOSITORY_NAME);
        svnAdmin.createRepository(REPOSITORY_NAME);
    }

    @After
    public void after() throws Exception {
//        ISvnAdmin svnAdmin = new SvnAdminForLocale();
//        svnAdmin.deleteRepository(REPOSITORY_NAME);
    }

    /**
     * Method: mkdir(String dirPath, String message)`
     */
    @Test
    public void testMakeDir() {
        SvnUser svnUser = new SvnUser("marssvn", "marssvn");
        ISvnClient svnClient = new SvnClient();
        svnClient.setRootPath("svn://localhost/" + REPOSITORY_NAME);
        svnClient.setSvnUser(svnUser);
        svnClient.mkdir("111/222/333", "test make dir 111/222/333");
        svnClient.mkdir("demo", "test make dir demo");
        svnClient.mkdir("src", "test make dir src");
    }
}
