package org.cytoscape.PModel.internal.Tasks;

import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.cytoscape.application.CyApplicationManager;

public class ResetNode extends AbstractTask {
	private CyNetworkView netView;
	private CyApplicationManager applicationManager;
	private CyServiceRegistrar registrar;
	public static final String IMAGE_COLUMN = "Image URL";
	public String columnName = "setBool";
	private CyNetwork network;

	public ResetNode(CyNetworkView netView, CyServiceRegistrar registrar, CyNetwork network) { 
		this.network = network;
		this.registrar = registrar;
		this.netView = netView;
		
	}
	
	String two = "http://i.imgur.com/0gmeOHH.png";
	String one = "http://i.imgur.com/tdPe5At.png";
	String zero = "http://i.imgur.com/tu2XzsP.png";
	String negone = "http://i.imgur.com/y0845Hl.png";
	String negtwo = "http://i.imgur.com/R7PVjVw.png";
	String plusplus = "http://i.imgur.com/S7RykhX.png";
	String negneg = "http://i.imgur.com/Y3j8Jl0.png";
	
	public void run(TaskMonitor monitor) {
		
		monitor.setTitle("Adding activation Node values");

		CyTable nodeTable = network.getDefaultNodeTable();
		
		if (nodeTable.getColumn(IMAGE_COLUMN) == null) {
			nodeTable.createColumn(IMAGE_COLUMN, String.class, false);
		}
		
		VisualMappingManager vmm = registrar.getService(VisualMappingManager.class);

		Set<VisualLexicon> lexSet = vmm.getAllVisualLexicon();
		VisualProperty<?> cgProp = null;
		for (VisualLexicon vl : lexSet) {
			cgProp = vl.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_1");
			if (cgProp != null)
				break;
		}
		if (cgProp == null) {
			System.out.println("Can't find the CUSTOMGRAPHICS visual property!!!!");
			return;
		}

		VisualStyle style = vmm.getVisualStyle(netView);

		if (nodeTable.getColumn(columnName) == null) {
			nodeTable.createColumn(columnName, Integer.class, false);
			
			List<CyNode> Nodes = CyTableUtil.getNodesInState(network, "selected", true);
			monitor.setStatusMessage("Warning: null column - creating column and setting values");

			for (CyNode node : Nodes) {
				nodeTable.getRow(node.getSUID()).set(columnName, Integer.valueOf(0));
				network.getRow(node).set(IMAGE_COLUMN, zero); 
			}
		} 
		
		if (nodeTable.getColumn(columnName) != null) {
			List<CyNode> Nodes = CyTableUtil.getNodesInState(network, "selected", true);
			monitor.setStatusMessage("Setting values");

			for (CyNode node : Nodes) {
				nodeTable.getRow(node.getSUID()).set(columnName, Integer.valueOf(0));
				network.getRow(node).set(IMAGE_COLUMN, zero); 
			}

		} 
		
		VisualMappingFunctionFactory factory = registrar.getService(VisualMappingFunctionFactory.class,
				"(mapping.type=passthrough)");
		PassthroughMapping map = (PassthroughMapping) factory.createVisualMappingFunction(IMAGE_COLUMN, String.class,
				cgProp);
		
		style.addVisualMappingFunction(map);
		style.apply(netView);
		netView.updateView();
	} 
}