package pl.polskieligi.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.polskieligi.model.Project;
import pl.polskieligi.model.Team;

@Repository
@Transactional
public class TeamDAOImpl extends AbstractDAOImpl<Team> implements TeamDAO {
	public TeamDAOImpl() {
		super(Team.class);
	}

	public Team saveOrUpdate(Team team) {
		Session session = getCurrentSession();
		Query query = session.createQuery("from Team where minut_id = :minut_id");
		query.setParameter("minut_id", team.getMinut_id());
		Team oldTeam = null;
		@SuppressWarnings("unchecked")
		List<Team> teams = query.list();
		for (Team t : teams) {
			oldTeam = t;
			if (team.getName() != null && !team.getName().isEmpty()) {
				oldTeam.setName(team.getName());
			}
			session.update(oldTeam);
			return oldTeam;
		}
		session.save(team);
		return team;

	}

	@SuppressWarnings("unchecked")
	public List<Team> getTeams() {
		List<Team> result = null;
		Session session = getCurrentSession();

		SQLQuery query = session.createSQLQuery(
				"SELECT t.* FROM  team_leagues tl join teams t on t.id = tl.team_id join projects p on p.id = tl.project_id where p.archive = :archive and p.type = :type and p.published = :published");
		query.addEntity(Team.class);
		query.setParameter("archive", false);
		query.setParameter("type", Project.REGULAR_LEAGUE);
		query.setParameter("published", true);
		result = query.list();

		return result;
	}
}
