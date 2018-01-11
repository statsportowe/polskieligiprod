package pl.polskieligi.dao;

import java.sql.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.polskieligi.model.Round;

@Repository
@Transactional
public class RoundDAOImpl extends AbstractDAOImpl<Round> implements RoundDAO {
	public RoundDAOImpl() {
		super(Round.class);
	}

	public Round saveOrUpdate(Round round) {
		Session session = getCurrentSession();
		Query query = session.createQuery("from Round where project_id = :project_id and matchcode = :matchcode");
		query.setParameter("project_id", round.getProject().getId());
		query.setParameter("matchcode", round.getMatchcode());
		Round oldRound = null;
		@SuppressWarnings("unchecked")
		List<Round> rounds = query.list();
		for (Round r : rounds) {
			oldRound = r;
			if (round.getName() != null && !round.getName().isEmpty()) {
				oldRound.setName(round.getName());
			}
			if (round.getRound_date_first() != null && !round.getRound_date_first().equals(new Date(0))) {
				oldRound.setRound_date_first(round.getRound_date_first());
			}
			if (round.getRound_date_last() != null && !round.getRound_date_last().equals(new Date(0))) {
				oldRound.setRound_date_last(round.getRound_date_last());
			}

			session.update(oldRound);
			return oldRound;
		}
		
		session.save(round);
		return round;
	}
}
