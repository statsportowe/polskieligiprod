package pl.polskieligi.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.polskieligi.model.Match;
import pl.polskieligi.model.Team;
import pl.polskieligi.model.TeamLeague;

@Repository
@Transactional
public class TeamLeagueDAOImpl  extends AbstractDAOImpl<TeamLeague> implements TeamLeagueDAO{
	public TeamLeagueDAOImpl() {
		super(TeamLeague.class);
	}
	public TeamLeague saveOrUpdate(TeamLeague teamLeague) {		
		Session session = getCurrentSession();
			Query query = session
					.createQuery("from TeamLeague where project_id = :project_id AND team_id = :team_id");
			query.setParameter("project_id", teamLeague.getProject().getId());
			query.setParameter("team_id", teamLeague.getTeam().getId());
			@SuppressWarnings("unchecked")
			List<TeamLeague> leagues = query.list();
			for (TeamLeague tl : leagues) {
				return tl;
			}
			session.save(teamLeague);
			
			
		return teamLeague;
	}

	public void updateTeams(Long projectId,
			Collection<Long> teamIds) {

		Session session = getCurrentSession();

			Query query = session
					.createQuery("from TeamLeague where project_id = :project_id AND team_id NOT IN (:team_id)");
			query.setParameter("project_id", projectId);
			query.setParameterList("team_id", teamIds);

			@SuppressWarnings("unchecked")
			List<TeamLeague> teamsToDelete = query.list();
			for (TeamLeague tl : teamsToDelete) {							
				query = session
						.createQuery("from Match where project_id = :project_id AND (matchpart1_id = :matchpart1 or matchpart2_id = :matchpart2)");
				query.setParameter("project_id", projectId);
				query.setParameter("matchpart1", tl.getTeam().getId());
				query.setParameter("matchpart2", tl.getTeam().getId());

				@SuppressWarnings("unchecked")
				List<Match> matchesToDelete = query.list();
				for (Match m : matchesToDelete) {
					session.delete(m);
				}
				session.delete(tl);
			}
			
	}
	
	@SuppressWarnings("unchecked")
	public List<Team> getTeams(Long projectId) {
		List<Team> result = null;
		Session session = getCurrentSession();

			Query query = session.createQuery("from Team t where exists ( from TeamLeague tl where t.id = tl.team.id and tl.project.id = :project_id) order by t.name");
			query.setParameter("project_id", projectId);
			result = query.list();			
			
		return result;
	}
}
