package com.iamhusrev.repository;

import com.iamhusrev.entity.Project;
import com.iamhusrev.entity.Task;
import com.iamhusrev.entity.User;
import com.iamhusrev.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {


    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.projectCode = ?1 AND t.taskStatus <> 'COMPLETE' ")
    int totalNonCompletedTasks(String projectCode);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.projectCode = ?1 AND t.taskStatus = com.iamhusrev.enums.Status.COMPLETE")
    int totalCompletedTasks(String projectCode);

    List<Task> findAllByProject(Project project);

    List<Task> findAllByTaskStatusIsNotAndAssignedEmployee(Status status, User assignedEmployee);

    List<Task> findAllByTaskStatusAndAssignedEmployee(Status status, User assignedEmployee);

    List<Task> findAllByAssignedEmployee(User assignedEmployee);

}
