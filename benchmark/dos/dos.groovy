perror = System.err.&println
now = System.&currentTimeMillis

start_v_name = "100";
edges_file = args[0]
graph_type = args[1]
EDGE_LABEL = 'basic_edge'

if(graph_type == 'tinkergraph') {
  graph = TinkerGraph.open()
} else if(graph_type == 'neo4j') {
  graph = Neo4jGraph.open('data/neo4j_ce')
} else {
  throw new IllegalArgumentException('invalid graph system')
}

g = graph.traversal()

s = new Scanner(new File(edges_file));
names = new HashSet<String>();

startTime = System.currentTimeMillis()
k=0
while(s.hasNextInt()) {
	++k;
	if(k % 1000 == 0) println(k);
	i = s.nextInt().toString();
	j = s.nextInt().toString();
	if(!names.contains(i)) v1 = g.addV(LABEL_V).property(NAME, i).next();
	else v1 = g.V().has(NAME, i).next();

	if(!names.contains(j)) v2 = g.addV(LABEL_V).property(NAME, j).next();
	else v2 = g.V().has(NAME, j).next();

	names.add(i);
	names.add(j);

	g.V(v1).addE(LABEL_E).to(v2).iterate();
}

endTime = System.currentTimeMillis()
timeDiff = endTime - startTime

System.err.println('ingest time: ' + (timeDiff / 1000.0).toString() + 'seconds.')

v = g.V().has('NAME', start_v_name).next();
println('Calculating 3 degrees of separation from vertex ' + start_v_name)
start = now()
count = g.V(v).out().dedup().out().dedup().out().dedup().count()
end = now()
println(count)
elapsed = end - start
perror('dos time: ' + (elapsed/1000.0).toString())

println('Calculating 3 degrees of separation from vertex ' + start_v_name + ' using repeat() step')
start = now()
count = g.V(v).repeat(out().dedup()).times(3).count()
end = now()
println(count)
elapsed = end - start
perror('dos time (with repeat): ' + (elapsed/1000.0).toString())
