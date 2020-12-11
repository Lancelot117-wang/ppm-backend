package com.wjh.ppmtool.service;

import com.wjh.ppmtool.domain.Backlog;
import com.wjh.ppmtool.domain.Project;
import com.wjh.ppmtool.domain.User;
import com.wjh.ppmtool.exception.ProjectIdException;
import com.wjh.ppmtool.exception.ProjectNotFoundException;
import com.wjh.ppmtool.repository.BacklogRepository;
import com.wjh.ppmtool.repository.ProjectRepository;
import com.wjh.ppmtool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project, String username){

        if(project.getId() != null){
            Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());

            if(existingProject != null && (!existingProject.getProjectLeader().equals(username))){
                throw new ProjectNotFoundException("Project not found in your account");
            }else if(existingProject == null){
                throw new ProjectNotFoundException("Project with ID: '"+existingProject.getProjectIdentifier()+"' cannot be updated");
            }
        }

        try{
            User user = userRepository.findByUsername(username);
            project.setUser(user);
            project.setProjectLeader(user.getUsername());

            if(project.getId()==null) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(project.getProjectIdentifier());
            }else{
                project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier()));
            }
            return projectRepository.save(project);
        }catch(Exception e){
            throw new ProjectIdException("Project ID '"+project.getProjectIdentifier()+"' already exists");
        }
    }

    public Project findProjectByIdentifier(String projectId, String username){

        Project project = projectRepository.findByProjectIdentifier(projectId);

        if(project == null){
            throw new ProjectIdException("Project ID '"+projectId+"' does not exist");
        }

        if(!project.getProjectLeader().equals(username)){
            throw new ProjectNotFoundException("Project not found in your account");
        }

        return project;
    }

    public Iterable<Project> findAllProjects(String username){
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectid, String username){
        /*Project project = projectRepository.findByProjectIdentifier(projectid);

        if(project == null){
            throw new ProjectIdException("Cannot delete project with ID '"+projectid+"'. This project does not exist");
        }*/

        projectRepository.delete(findProjectByIdentifier(projectid, username));
    }
}
