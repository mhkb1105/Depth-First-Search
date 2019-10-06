package A4Q2;
import java.util.*;

/**
 * Represents courses and prerequisites.  A binary search tree sorted by course 
 * number is used for fast access.  This links out to a directed graph representing
 * course dependencies (prerequisites).  The graph uses a hash table to represent
 * incoming and outgoing edges.
 * Incoming edges represent courses that are prerequisites for a course.
 * Outgoing edges represent courses for which the course is a prerequisite.
 * @param <E>
 * @param <V>
 */
public class Courses<E, V> {

    private TreeMap<CourseNumber, Course> courseMap; 
    private AdjacencyMapGraph<CourseNumber, CourseRequisite> courseGraph;

    public Courses() {
        courseMap = new TreeMap<>();
        courseGraph = new AdjacencyMapGraph<>(true); 
    }

    
    public Course getCourse(CourseNumber courseNum) {
        return courseMap.get(courseNum);
    }

    
    public Edge<CourseRequisite> getRequisite(CourseNumber course1, CourseNumber course2) {
     return courseGraph.getEdge(courseMap.get(course1).getCourseVertex(),courseMap.get(course2).getCourseVertex());
    }
    
    
    public Course putCourse(CourseNumber courseNum, String courseName) {
        Course course = courseMap.get(courseNum);
        if (course == null) { 
            Vertex<CourseNumber> vertex = courseGraph.insertVertex(courseNum); 
            course = new Course(courseNum,courseName,vertex);
            courseMap.put(courseNum, course); 
        } else {
            course.setCourseName(courseName); 
        }
        return course;
    }
  
   
    public Edge<CourseRequisite> putRequisite(CourseNumber courseNum1, 
            CourseNumber courseNum2, CourseRequisite requisite) 
            throws InvalidCourseNumberException, CircularPreRequisiteException {
    	
    	if(courseMap.get(courseNum1).getCourseVertex()==null || courseMap.get(courseNum2).getCourseVertex()==null) {
    		throw new InvalidCourseNumberException();
    		}
    	
    	Set<Vertex<CourseNumber>> known = new HashSet<>();
    	Map<Vertex<CourseNumber>,Edge<CourseRequisite>> forest = new ProbeHashMap<>();
    	DFS(courseGraph,courseMap.get(courseNum2).getCourseVertex(), known, forest);
    	for(Vertex<CourseNumber> courseNumVertex : known) {
    		if(courseNumVertex.equals(courseMap.get(courseNum1).getCourseVertex())) {
    			throw new CircularPreRequisiteException();
    		}
    	} 
    	courseGraph.insertEdge(courseMap.get(courseNum1).getCourseVertex(),courseMap.get(courseNum2).getCourseVertex(), requisite);

        return null;
   }
    
  /**
   * Performs depth-first search of the unknown portion of Graph g starting at Vertex u.
   *
   * @param g Graph instance
   * @param u Vertex of graph g that will be the source of the search
   * @param known is a set of previously discovered vertices
   * @param forest is a map from nonroot vertex to its discovery edge in DFS forest
   *
   * As an outcome, this method adds newly discovered vertices (including u) to the known set,
   * and adds discovery graph edges to the forest.
   */
  public static <V,E> void DFS(Graph<V,E> g, Vertex<V> u,
                    Set<Vertex<V>> known, Map<Vertex<V>,Edge<E>> forest) {
    known.add(u);                              
    for (Edge<E> e : g.outgoingEdges(u)) {     
      Vertex<V> v = g.opposite(u, e);
      
      if (!known.contains(v)) {
        forest.put(v, e);                      
        DFS(g, v, known, forest);              
      }
    }
  }
    
   /**
   * Returns a string representation of the courses.
   */
  public String toString() {
      Iterable<Entry<CourseNumber, Course>> courses = courseMap.entrySet();
      String courseMapEntries = new String("Courses: \n");
      for (Entry<CourseNumber, Course> course : courses) {
          courseMapEntries = courseMapEntries + course.getValue().toString() + "\n" ;
      }
      return (courseMapEntries + courseGraph.toString() + "\n" );
  }
}
