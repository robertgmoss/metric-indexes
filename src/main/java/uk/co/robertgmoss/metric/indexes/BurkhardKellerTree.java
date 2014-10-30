/*  
 *  Copyright (C) 2014 Robert Moss
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package uk.co.robertgmoss.metric.indexes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.robertgmoss.metric.Element;

/**
 * This data structure is good for when distances are discrete values
 * @author Robert Moss
 *
 * @param <M>
 */
public class BurkhardKellerTree<M extends Element<M>> implements Serializable {

	private static final long serialVersionUID = -7999079384931213505L;
	private Node root;

	private class Node implements Serializable {
		M data;
		Map<Double, Node> children = new HashMap<Double, Node>();
	}

	public BurkhardKellerTree(M... points) {
		this(Arrays.asList(points));
	}

	public BurkhardKellerTree(List<M> points) {
		for (M point : points) {
			root = insert(point, root);
		}
	}

	private Node insert(M point, Node n) {
		if (n == null) {
			n = new Node();
			n.data = point;
			return n;
		}

		Double distance = point.distance(n.data);
		Node subTree = insert(point, n.children.get(distance));
		n.children.put(distance, subTree);
		return n;
	}

	public List<M> rangeQuery(M element, double radius) {
		List<M> returnSet = new ArrayList<M>();
		rangeQuery(element, radius, returnSet, root);
		return returnSet;

	}

	private void rangeQuery(M element, double radius, List<M> returnSet, Node node) {
		double distance = element.distance(node.data);
		if (distance <= radius) {
			returnSet.add(node.data);
		}
		for (Double distanceToChild : node.children.keySet()) {
			if ((distance - distanceToChild <= radius) && (distanceToChild - distance <= radius)) {
				Node child = node.children.get(distanceToChild);
				rangeQuery(element, radius, returnSet, child);
			}
		}
	}

}
