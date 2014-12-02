/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jbpm.bugzilla.services;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugFactory;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.rpc.LogIn;
import com.j2bugzilla.rpc.ReportBug;

/**
 *
 * @author salaboy
 */
public class NewBugWorkItemHandler implements WorkItemHandler{

    public NewBugWorkItemHandler() {
    }

    
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    	
		System.out.println("Connectandose a Bugzilla");
		BugzillaConnector connector = new BugzillaConnector();
		try {
			connector.connectTo("http://127.0.0.1/bugzilla", "emilianoandre@gmail.com", "emi123");
			LogIn logIn = new LogIn("emilianoandre@gmail.com", "emi123");
			connector.executeMethod(logIn);

		
			BugFactory factory = new BugFactory();
			Bug bug = factory.newBug()
			    .setOperatingSystem((String)workItem.getParameter("inOperatingSystem"))
			    .setPlatform("PC")
			    .setPriority((String)workItem.getParameter("inPriority"))
			    .setProduct((String)workItem.getParameter("inProduct"))
			    .setComponent((String)workItem.getParameter("inComponent"))
			    .setSummary((String)workItem.getParameter("inSummary"))
			    .setVersion((String)workItem.getParameter("inVersion"))
			    .setDescription((String)workItem.getParameter("inDescription"))
			    .createBug();
        
	        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
	        KieSession ksession = kc.newKieSession("BugzillaSession");
	        ksession.insert(bug);
	        ksession.fireAllRules();
		
			ReportBug report = new ReportBug(bug);
	
			connector.executeMethod(report);
			
			int id = report.getID();
			System.out.println("Bug creado con id= " + id);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		manager.completeWorkItem(workItem.getId(), null);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
