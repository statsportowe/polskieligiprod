package pl.polskieligi.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.polskieligi.model.League;

@Repository
@Transactional
public class LeagueDAOImpl extends AbstractDAOImpl<League> implements LeagueDAO {

	public LeagueDAOImpl() {
		super(League.class);
	}

	public League saveOrUpdate(League league) {
		Session session = getCurrentSession();
		Query query = session.createQuery("from League where name = :name");
		query.setParameter("name", league.getName());
		@SuppressWarnings("unchecked")
		List<League> leagues = query.list();
		for (League l : leagues) {
			return l;
		}
		session.saveOrUpdate(league);
		return league;
	}
}
