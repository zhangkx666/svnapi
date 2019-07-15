import com.marssvn.svnapi.ISvnAdmin;
import com.marssvn.svnapi.SvnAdminForLocale;
import com.marssvn.svnapi.exception.SvnApiException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SvnAdminForLocaleTest {

    /**
     * create repository
     */
    @Test
    public void test01_CreateRepository() throws Exception {
        ISvnAdmin svnAdmin = new SvnAdminForLocale();
        String repoName = "test_2019_01";
        svnAdmin.deleteRepository(repoName);
        svnAdmin.createRepository(repoName);
    }

    @Test(expected = SvnApiException.class)
    public void test02_RestartSvnserve() throws IOException {
        ISvnAdmin svnAdmin = new SvnAdminForLocale();
        svnAdmin.restartSvnserve(null);
    }

    /**
     * throw SvnApiException when repository exists
     */
    @Test(expected = SvnApiException.class)
    public void test03_CreateExistsRepository() throws Exception {
        ISvnAdmin svnAdmin = new SvnAdminForLocale();
        String repoName = "test_2019_01";

        svnAdmin.createRepository(repoName);
    }

    /**
     * move repository
     */
    @Test
    public void test04_MoveRepository() throws Exception {
        ISvnAdmin svnAdmin = new SvnAdminForLocale();
        String rootPath = System.getProperty("user.home") + "/svn/";
        String oldRepoName = "test_2019_01";
        String newRepoName = "test_2019_02";
        File repo = new File(rootPath + oldRepoName);
        Assert.assertTrue(repo.exists());
        svnAdmin.moveRepository(oldRepoName, newRepoName);
        Assert.assertFalse(repo.exists());
        repo = new File(rootPath + newRepoName);
        Assert.assertTrue(repo.exists());
    }

    /**
     * delete repository
     */
    @Test
    public void test05_DeleteRepository() throws Exception {
        ISvnAdmin svnAdmin = new SvnAdminForLocale();
        String rootPath = System.getProperty("user.home") + "/svn/";
        String repoName = "test_2019_02";
        File repo = new File(rootPath + repoName);
        Assert.assertTrue(repo.exists());
        svnAdmin.deleteRepository(repoName);
        repo = new File(rootPath + repoName);
        Assert.assertFalse(repo.exists());
    }
}
