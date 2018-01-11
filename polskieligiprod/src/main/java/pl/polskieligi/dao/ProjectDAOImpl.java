package pl.polskieligi.dao;

import java.sql.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.polskieligi.model.Project;

@Repository
@Transactional
public class ProjectDAOImpl extends AbstractDAOImpl<Project> implements ProjectDAO {
	public ProjectDAOImpl() {
		super(Project.class);
	}

	public Project retrieveProjectByMinut(Integer minutId) {
		Project result = null;
		Session session = getCurrentSession();

		Query query = session.createQuery("from Project where minut_id = :minut_id");
		query.setParameter("minut_id", minutId);
		@SuppressWarnings("unchecked")
		List<Project> projects = query.list();
		for (Project p : projects) {
			result = p;
		}

		return result;
	}

	public Project saveOrUpdate(Project leagueProject) {
		Session session = getCurrentSession();
		Query query = session.createQuery(
				"from Project where minut_id = :minut_id or (league.id = :league_id and season.id = :season_id)");
		query.setParameter("minut_id", leagueProject.getMinut_id());
		query.setParameter("league_id", leagueProject.getLeague().getId());
		query.setParameter("season_id", leagueProject.getSeason().getId());

		Project oldProject = null;
		@SuppressWarnings("unchecked")
		List<Project> leagues = query.list();
		for (Project l : leagues) {
			oldProject = l;
			if (leagueProject.getMinut_id() > 0) {
				oldProject.setMinut_id(leagueProject.getMinut_id());
			}
			if (leagueProject.getLeague() != null) {
				oldProject.setLeague(leagueProject.getLeague());
			}
			if (leagueProject.getSeason() != null) {
				oldProject.setSeason(leagueProject.getSeason());
			}
			if (leagueProject.getName() != null && !leagueProject.getName().isEmpty()) {
				oldProject.setName(leagueProject.getName());
			}
			if (leagueProject.getStart_date() != null && !leagueProject.getStart_date().equals(new Date(0))) {
				oldProject.setStart_date(leagueProject.getStart_date());
			}
			oldProject.setArchive(leagueProject.getArchive());
			if (leagueProject.getType() > 0) {
				oldProject.setType(leagueProject.getType());
			}

			session.update(oldProject);
			return oldProject;
		}
		session.save(leagueProject);

		return leagueProject;
	}

	public Project getLastProjectForTeam(Integer teamId) {
		Project result = null;
		Session session = getCurrentSession();
		SQLQuery query = session.createSQLQuery("SELECT p.* " + "FROM team_leagues tl "
				+ "LEFT JOIN projects p ON p.id = tl.project_id "
				+ "WHERE tl.team_id = :team_id and p.published = :published and p.type = :type AND p.start_date = ( "
				+ "SELECT MAX( p2.start_date ) " + "FROM team_leagues tl2 "
				+ "LEFT JOIN projects p2 ON p2.id = tl2.project_id "
				+ "WHERE tl2.team_id = tl.team_id and p2.published = :published and p2.type = :type )");
		query.addEntity(Project.class);
		query.setParameter("team_id", teamId);
		query.setParameter("published", true);
		query.setParameter("type", Project.REGULAR_LEAGUE);
		for (Object i : query.list()) {
			result = (Project) i;
		}

		return result;
	}

	public Long getOpenProjectsCount() {
		Session session = getCurrentSession();		
		Criteria cr = session.createCriteria(Project.class);
		cr.add(Restrictions.eq("type", new Integer(1)));
		cr.add(Restrictions.or(Restrictions.eq("archive", false), Restrictions.eq("published",false)));
		return (Long) cr.setProjection(Projections.rowCount()).uniqueResult();
	}
}
