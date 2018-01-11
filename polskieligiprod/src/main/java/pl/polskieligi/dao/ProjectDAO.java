package pl.polskieligi.dao;

import pl.polskieligi.model.Project;

public interface ProjectDAO extends AbstractDAO<Project>{
	public Project retrieveProjectByMinut(Integer minutId);
	public Project getLastProjectForTeam(Integer teamId);
	public Long getOpenProjectsCount();
}
