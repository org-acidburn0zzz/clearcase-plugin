package hudson.plugins.clearcase;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;
import hudson.util.VariableResolver;

import java.io.IOException;

public class ClearToolDynamicUCM extends ClearToolExec {

    private transient String viewDrive;

    public ClearToolDynamicUCM(VariableResolver variableResolver, ClearToolLauncher launcher, String viewDrive) {
        super(variableResolver, launcher);
        this.viewDrive = viewDrive;
    }

    @Override
    protected FilePath getRootViewPath(ClearToolLauncher launcher) {
        return new FilePath(launcher.getWorkspace().getChannel(), viewDrive);
    }

    public void setcs(String viewName, String configSpec) throws IOException, InterruptedException {
        launcher.getListener().fatalError("Dynamic UCM view does not support setcs with a config spec");
    }
    
    /**
     * Syncronize the dynamic view with the latest recomended baseline for the stream.
     * 1. Generated a new config spec
     * 2. Set the config spec on the view
     */
    public void syncronizeViewWithStream(String viewName, String stream) throws IOException, InterruptedException {
        chstream(viewName, stream);
        setcs(viewName);
    }
    
    /**
     * The config spec of the Dynamic UCM must be updated if a new baseline is recomended.
     * Therefore chstream is executed to regenerate the config spec it must then be set on
     * the view
     * 
     * @see http://www.ipnom.com/ClearCase-Commands/chstream.html
     */
    private void chstream(String viewName, String stream) throws IOException, InterruptedException {
        ArgumentListBuilder cmd = new ArgumentListBuilder();
        cmd.add("chstream");
        cmd.add("-generate");
        cmd.add("stream:" + stream);
        
        FilePath viewPath = getRootViewPath(launcher).child(viewName);
        launcher.run(cmd.toCommandArray(), null, null, viewPath);
    }
    
    /**
     * The view tag does need not be active.
     * However, it is possible to set the config spec of a dynamic UCM view from within a snapshot view
     * using "-tag view-tag -stream"
     * 
     * This will only have an effect if chstream is executed first
     * 
     * @see http://www.ipnom.com/ClearCase-Commands/setcs.html
     */
    private void setcs(String viewName) throws IOException, InterruptedException {
        ArgumentListBuilder cmd = new ArgumentListBuilder();
        cmd.add("setcs");
        cmd.add("-tag");
        cmd.add(viewName);
        cmd.add("-stream");
        launcher.run(cmd.toCommandArray(), null, null, null);
    }

    public void mkview(String viewName, String streamSelector) throws IOException, InterruptedException {
        launcher.getListener().fatalError("Dynamic UCM view does not support mkview");        
    }

    public void rmview(String viewName) throws IOException, InterruptedException {
        launcher.getListener().fatalError("Dynamic UCM view does not support rmview");
    }

    public void update(String viewName, String loadRules) throws IOException, InterruptedException {
        launcher.getListener().fatalError("Dynamic UCM view does not support update");
    }

    public void startView(String viewTags)  throws IOException, InterruptedException {
        ArgumentListBuilder cmd = new ArgumentListBuilder();
        cmd.add("startview");
        cmd.addTokenized(viewTags);
        launcher.run(cmd.toCommandArray(), null, null, null);
    }

}
