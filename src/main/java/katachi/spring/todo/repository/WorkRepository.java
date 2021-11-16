package katachi.spring.todo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import katachi.spring.todo.model.Work;

@Repository
public interface WorkRepository extends JpaRepository<Work, Integer> {
	@Query(value = "SELECT w FROM Work w WHERE w.name LIKE %:name%")
	public List<Work> findWorks(@Param("name") String name);

	@Query(value = "SELECT w FROM Work w ORDER BY w.id")
	public List<Work> findAll();
	public Work findById(int workId);
	public List<Work> deleteById(int workId);
}
