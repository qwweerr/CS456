package router;

class link {
	public int link_id;
	public int cost;
	public int router_id;

	public link(int rid, int lid, int cost) {
		this.router_id = rid;
		this.link_id = lid;
		this.cost = cost;
	}

	public link(linkcost lc) {
		link_id = lc.link;
		cost = lc.cost;
	}
}