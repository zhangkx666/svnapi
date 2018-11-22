import com.marssvn.svnapi.ISVNAdmin;
import com.marssvn.svnapi.SVNAdminImpl;
import com.marssvn.svnapi.exception.SVNException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class SVNAdminImplTest {

    /**
     * create repository
     *
     * @throws Exception
     */
    @Test
    public void testCreateRepository_01() throws Exception {
        ISVNAdmin svnAdmin = new SVNAdminImpl();
        String repoName = "test_2018_01";
        svnAdmin.deleteRepository(System.getProperty("user.home") + "/svn_reps/" + repoName);
        svnAdmin.createRepository(null, repoName);
    }

    /**
     * throw SVNException when repository exists
     */
    @Test(expected = SVNException.class)
    public void testCreateRepository_02() throws Exception {
        ISVNAdmin svnAdmin = new SVNAdminImpl();
        String repoName = "test_2018_01";

        svnAdmin.createRepository(null, repoName);
    }

    /**
     * move repository
     */
    @Test
    public void testCreateRepository_03() throws Exception {
        ISVNAdmin svnAdmin = new SVNAdminImpl();
        String rootPath = System.getProperty("user.home") + "/svn_reps/";
        String oldRepoName = "test_2018_01";
        String newRepoName = "test_2018_02";
        File repo = new File(rootPath + oldRepoName);
        Assert.assertTrue(repo.exists());
        svnAdmin.moveRepository(rootPath, oldRepoName, newRepoName);
        Assert.assertFalse(repo.exists());
        repo = new File(rootPath + newRepoName);
        Assert.assertTrue(repo.exists());
    }

    /**
     * delete repository
     */
    @Test
    public void testCreateRepository_04() throws Exception {
        ISVNAdmin svnAdmin = new SVNAdminImpl();
        String repoPath = System.getProperty("user.home") + "/svn_reps/" + "test_2018_02";
        File repo = new File(repoPath);
        Assert.assertTrue(repo.exists());
        svnAdmin.deleteRepository(repoPath);
        repo = new File(repoPath);
        Assert.assertFalse(repo.exists());
    }
}
