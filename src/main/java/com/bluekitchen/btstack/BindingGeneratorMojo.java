package com.bluekitchen.btstack;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * This MOJO will generate the java classes used by BTStack from the BTStack
 * source code.
 *
 * @author flacy
 */
@Mojo(defaultPhase = LifecyclePhase.GENERATE_SOURCES, name = "BindingGenerator")
@Execute(goal = "BindingGenerator")
public class BindingGeneratorMojo extends AbstractMojo {

    private static final String HCI_CMDS_H = "include/btstack/hci_cmds.h";
    private static final String HCI_CMDS_C = "src/hci_cmds.c";
    private static final String HCI_H = "src/hci.h";

    /**
     * This is the directory into which the generated java source will be
     * placed.
     */
    @Parameter(defaultValue = "target/generated-sources/bindingGenerator", required = true)
    private File outputDirectory;

    /**
     * This is the directory containing the checked-out 'C' source for BTStack.
     */
    @Parameter(required = true, alias = "BTStackRoot")
    private File btStackRoot;

    /**
     * This is a reference to the java project so that the MOJO can add the
     * output directory to the source path.
     */
    @Parameter(property = "project")
    private MavenProject project;

    public BindingGeneratorMojo() {
        super();
    }

    @Override
    // Package-private access for junit tests only
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            DefinesParser dp = new DefinesParser();
            dp.read(new File(btStackRoot, HCI_CMDS_H));
            dp.read(new File(btStackRoot, HCI_H));

            CommandParser cp = new CommandParser();
            cp.read(new File(btStackRoot, HCI_CMDS_C));

            EventsParser ep = new EventsParser();
            ep.read(new File(btStackRoot, HCI_CMDS_H));

            if (project != null) {
                project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
            }
            
            BindingFormatter bf = new BindingFormatter(outputDirectory);
            bf.format(dp, ep, cp);
            
        } catch (IOException ex) {
            throw new MojoExecutionException("Unable to generate java source.", ex);
        } catch (NullPointerException ex) {
            throw new MojoExecutionException("Unable to generate java source.", ex);
        }

    }

    // Package-private access for junit tests only
    /**
     * @param outputDirectory the outputDirectory to set
     */
    void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @param btStackRoot the btStackRoot to set
     */
    void setBtStackRoot(File btStackRoot) {
        this.btStackRoot = btStackRoot;
    }

}
