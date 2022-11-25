package map_grind;

import java.util.ArrayList;
import java.util.HashMap;

import main.XY;

/**
 * Data structure representing the map. Very inefficient implementation but whatever.
 * @author Jimmy Sheng
 */
public class Graph {
	// graph ADT
	public HashMap<XY, TileVertex> vertexList;
	
	// extra infos
	public String name;
	public int mobFleetBattleCount;
	public XY entry;
	public int entry_color;
	
	public HashMap<XY, Tile> startPositions;
	public HashMap<XY, Integer> startColors;
	
	public XY initialOffset;
	
	public HashMap<String, Integer> prio;					// priority addValue mapping
	
	// grinder choice infos
	public boolean onefleet;
	public boolean noboss;
	
	public Graph() {
		this.vertexList = new HashMap<XY, TileVertex>();
		
		this.startPositions = new HashMap<XY, Tile>();
		this.startColors = new HashMap<XY, Integer>();
		
		this.initialOffset = new XY(0, 0);
		
		this.prio = new HashMap<String, Integer>();
		
		this.onefleet = false;
		this.noboss = false;
	}
	
	/**
	 * Return the tile at the given pos.
	 * @param pos
	 * @return
	 */
	public Tile getTile(XY pos) {
		if (this.vertexList.containsKey(pos)) {
			return this.vertexList.get(pos).getTile();
		}
		return null;
	}
	
	/**
	 * Set the tile at the given pos.
	 * @param pos
	 * @param tile
	 * @return
	 */
	public boolean setTile(XY pos, Tile tile) {
		if (this.vertexList.containsKey(pos)) {
			this.vertexList.get(pos).setTile(tile);
			return true;
		}
		return false;
	}
	
	/**
	 * Add a vertex at the given position with the given tile.
	 * @param pos
	 * @param tile
	 * @return
	 */
	public boolean addVertex(XY pos, Tile tile) {
		if (!this.vertexList.containsKey(pos)) {
			TileVertex v = new TileVertex(pos, tile);
			this.vertexList.put(pos, v);
			return true;
		}
		return false;
	}
	
	/**
	 * Remove the vertex at the given pos as well as cleaning up the edges into it.
	 * @param pos
	 * @return
	 */
	public boolean removeVertex(XY pos) {
		if (this.vertexList.containsKey(pos)) {
			this.removeEdgesInto(pos);
			this.vertexList.remove(pos);
			return true;
		}
		return false;
	}
	
	/**
	 * Add a directed edge from pos to next.
	 * @param pos
	 * @param next
	 * @return
	 */
	public boolean addEdge(XY pos, XY next) {
		if (this.vertexList.containsKey(pos) && this.vertexList.containsKey(next)) {
			TileVertex v = this.vertexList.get(pos);
			return v.addEdge(next);
		}
		return false;
	}
	
	/**
	 * Remove the edge from pos to next if it exists.
	 * @param pos
	 * @param next
	 * @return
	 */
	public boolean removeEdge(XY pos, XY next) {
		if (this.vertexList.containsKey(pos) && this.vertexList.containsKey(next)) {
			TileVertex v = this.vertexList.get(pos);
			return v.remEdge(next);
		}
		return false;
	}
	
	/**
	 * Return ArrayList of all links from the given pos.
	 * @param pos
	 * @return
	 */
	public ArrayList<XY> getLinks(XY pos) {
		return this.vertexList.get(pos).getLinks();
	}
	
	/**
	 * Return ArrayList of positions that has edge into the vertex at the given pos.
	 * @param pos
	 * @return
	 */
	public ArrayList<XY> getEdgesInto(XY pos) {
		ArrayList<XY> linksIn = new ArrayList<XY>();
		for (XY prev : this.vertexList.keySet()) {
			TileVertex v = this.vertexList.get(prev);
			if (v.containsLink(pos)) {
				linksIn.add(v.getPos());
			}
		}
		return linksIn;
	}
	
	/**
	 * Remove all edges pointing into the given pos. Called only by removeVertex().
	 * @param pos
	 * @return
	 */
	private void removeEdgesInto(XY pos) {
		ArrayList<XY> linksIn = this.getEdgesInto(pos);
		for (XY prev : linksIn) {
			this.vertexList.get(prev).remEdge(pos);
		}
	}
	
	/**
	 * Return true if the vertex at given pos has been visited, false otherwise.
	 * @param pos
	 * @return
	 */
	public boolean getVisited(XY pos) {
		if (this.vertexList.containsKey(pos)) {
			return (this.vertexList.get(pos)).visited;
		}
		return false;
	}
	
	/**
	 * Sets visited to true/false for the given pos.
	 * @param pos
	 * @param b
	 * @return
	 */
	public boolean setVisited(XY pos, boolean b) {
		if (this.vertexList.containsKey(pos)) {
			(this.vertexList.get(pos)).visited = b;
			return true;
		}
		return false;
	}
	
	/**
	 * Set all vertices to unvisited state.
	 */
	public void resetVisited() {
		for (TileVertex v : this.vertexList.values()) {
			v.visited = false;
		}
	}
	
	/**
	 * Return true if vertex at given pos is obstructed (i.e. has an enemy), false otherwise.
	 * @param pos
	 * @return
	 */
	public boolean getObstructed(XY pos) {
		if (this.vertexList.containsKey(pos)) {
			return (this.vertexList.get(pos).obstructed);
		}
		return false;
	}
	
	/**
	 * Set obstructed to true/false at the given pos
	 * @param pos
	 * @return
	 */
	public boolean setObstructed(XY pos, boolean b) {
		if (this.vertexList.containsKey(pos)) {
			(this.vertexList.get(pos)).obstructed = b;
			return true;
		}
		return false;
	}
	
	/**
	 * Set all vertices to unobstructed state.
	 */
	public void resetObstructed() {
		for (TileVertex v : this.vertexList.values()) {
			v.obstructed = false;
		}
	}
	
	/**
	 * Tile wrapped in a graph vertex.
	 * @author Jimmy Sheng
	 */
	class TileVertex {
		private XY pos;
		private Tile tile;
		private ArrayList<XY> links;
		private boolean visited;
		private boolean obstructed;
		
		TileVertex (XY pos, Tile tile) {
			this.pos = pos;
			this.tile = tile;
			this.links = new ArrayList<XY>();
			this.visited = false;
			this.obstructed = false;
		}
		
		boolean addEdge(XY next) {
			if (!this.links.contains(next)) {
				this.links.add(next);
				return true;
			}
			return false;
		}
		
		boolean remEdge(XY next) {
			if (this.links.contains(next)) {
				this.links.remove(next);
				return true;
			}
			return false;
		}
		
		XY getPos() {
			return this.pos;
		}
		
		void setTile(Tile tile) {
			this.tile = tile;
		}
		
		Tile getTile() {
			return this.tile;
		}
		
		ArrayList<XY> getLinks() {
			return this.links;
		}
		
		boolean containsLink(XY next) {
			return this.links.contains(next);
		}
		
	}
	
}
