package pl.polskieligi.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.polskieligi.model.Season;

@Repository
@Transactional
public class SeasonDAOImpl extends AbstractDAOImpl<Season> implements SeasonDAO {
	public SeasonDAOImpl() {
		super(Season.class);
	}

	public Season saveOrUpdate(Season season) {
		Session session = getCurrentSession();
		Query query = session.createQuery("from Season where name = :name");
		query.setParameter("name", season.getName());
		@SuppressWarnings("unchecked")
		List<Season> leagues = query.list();
		for (Season s : leagues) {
			return s;
		}
		session.save(season);
		return season;
	}
}
