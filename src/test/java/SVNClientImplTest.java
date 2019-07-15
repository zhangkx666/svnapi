import com.marssvn.svnapi.ISvnAdmin;
import com.marssvn.svnapi.ISvnClient;
import com.marssvn.svnapi.SvnAdminForLocale;
import com.marssvn.svnapi.SvnClientImpl;
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

    @Before
    public void before() throws Exception {
        ISvnAdmin svnAdmin = new SvnAdminForLocale();
        String repoName = "marssvn_001";
        svnAdmin.deleteRepository(repoName);
        svnAdmin.createRepository(repoName);
    }

    @After
    public void after() throws Exception {
        ISvnAdmin svnAdmin = new SvnAdminForLocale();
        String repoName = "marssvn_001";
        svnAdmin.deleteRepository(repoName);
    }

    /**
     * Method: mkdir(String dirPath, String message)`
     */
    @Test
    public void testMakeDir() {
        SvnUser svnUser = new SvnUser("marssvn", "marssvn");
        ISvnClient svnClient = new SvnClientImpl();
        svnClient.setRootPath("svn://localhost/marssvn_001");
        svnClient.setSvnUser(svnUser);
        svnClient.mkdir("testMakeDir11/111/222/333", "test make dir");
    }
}
