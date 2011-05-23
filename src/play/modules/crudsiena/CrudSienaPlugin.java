package play.modules.crudsiena;

import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;

public class CrudSienaPlugin extends PlayPlugin {
    /**
     * Enhance this class
     * @param applicationClass
     * @throws java.lang.Exception
     */
    public void enhance(ApplicationClass applicationClass) throws Exception {
    	CrudSienaEnhancer.class.newInstance().enhanceThisClass(applicationClass);
    }
}
