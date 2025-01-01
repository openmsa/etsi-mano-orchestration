/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.service.graph;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;

import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;

public class GraphGenerator {
	private GraphGenerator() {
		// Nothing.
	}

	public static <V, E> BufferedImage drawGraph(final Graph<V, E> graph) {
		final JGraphXAdapter<V, E> graphAdapter = new JGraphXAdapter<>(graph);
		final mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter);
		layout.execute(graphAdapter.getDefaultParent());
		graphAdapter.getVertexToCellMap().entrySet().forEach(x -> {
			final Object[] lcell = Arrays.asList(x.getValue()).toArray();
			mxStyleUtils.setCellStyles(graphAdapter.getModel(), lcell, mxConstants.STYLE_ROUNDED, "true");
			final TaskVertex v = (TaskVertex) x.getValue().getValue();
			if (v.getStatus() == VertexStatusType.SUCCESS) {
				graphAdapter.setCellStyle("fillColor=7FFFD4", lcell);
			} else {
				graphAdapter.setCellStyle("fillColor=FF4040", lcell);
				mxStyleUtils.setCellStyles(graphAdapter.getModel(), lcell, mxConstants.STYLE_FONTCOLOR, "F0F0F0");
			}
		});
		return mxCellRenderer.createBufferedImage(graphAdapter, null, 1, new Color(255, 255, 255, 255), true, null);
	}

	public static <V, E> BufferedImage drawGraph2(final Graph<V, E> graph) {
		final JGraphXAdapter<V, E> graphAdapter = new JGraphXAdapter<>(graph);
		final mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter);
		layout.execute(graphAdapter.getDefaultParent());
		graphAdapter.getVertexToCellMap().entrySet().forEach(x -> {
			final Object[] lcell = Arrays.asList(x.getValue()).toArray();
			mxStyleUtils.setCellStyles(graphAdapter.getModel(), lcell, mxConstants.STYLE_ROUNDED, "true");
		});
		return mxCellRenderer.createBufferedImage(graphAdapter, null, 1, new Color(255, 255, 255, 255), true, null);
	}

}
