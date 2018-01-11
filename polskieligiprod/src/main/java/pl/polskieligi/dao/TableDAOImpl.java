package pl.polskieligi.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.polskieligi.controller.TableRow;
import pl.polskieligi.controller.TableRowMatch;
import pl.polskieligi.model.Team;

@Repository
@Transactional
public class TableDAOImpl implements TableDAO {
	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	TeamLeagueDAO teamLeagueDAO;

	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	private static int NUMBER_OF_LAST_MATCHES = 10;
	private final static String matchHomeQuery = "SELECT matchpart1.id, matchpart1.name, count( * ), "
			+ "SUM(m.matchpart1_result), " + "SUM(m.matchpart2_result), "
			+ "SUM(case when m.matchpart1_result > m.matchpart2_result then 1 else 0 end), "
			+ "SUM(case when m.matchpart1_result = m.matchpart2_result then 1 else 0 end), "
			+ "SUM(case when m.matchpart1_result < m.matchpart2_result then 1 else 0 end) " 
			+ "FROM Match AS m "
			+ "JOIN m.matchpart1 AS matchpart1 "
			+ "JOIN m.matchpart2 AS matchpart2 "
			+ "JOIN m.project AS project "
			+ "WHERE project.id = :project_id and m.published = :published and m.count_result = :count_result and matchpart1.id in (:team_ids) and matchpart2.id in (:team_ids) GROUP BY matchpart1.id, matchpart1.name";

	private final static String matchAwayQuery = "SELECT matchpart2.id, matchpart2.name, count( * ), "
			+ "SUM(m.matchpart2_result), " + "SUM(m.matchpart1_result), "
			+ "SUM(case when m.matchpart2_result > m.matchpart1_result then 1 else 0 end), "
			+ "SUM(case when m.matchpart2_result = m.matchpart1_result then 1 else 0 end), "
			+ "SUM(case when m.matchpart2_result < m.matchpart1_result then 1 else 0 end) " 
			+ "FROM Match AS m "
			+ "JOIN m.matchpart1 AS matchpart1 "
			+ "JOIN m.matchpart2 AS matchpart2 "
			+ "JOIN m.project AS project "
			+ "WHERE project.id = :project_id and m.published = :published and m.count_result = :count_result and matchpart1.id in (:team_ids) and matchpart2.id in (:team_ids) GROUP BY matchpart2.id, matchpart2.name";

	private static final String lastMatchesQuery = "select m.match_date, m.matchpart1_result as result1, m.matchpart2_result as result2, matchpart1.name as name1, matchpart2.name as name2, matchpart1.id "
			+ "from Match m "
			+ "JOIN m.matchpart1 AS matchpart1 "
			+ "JOIN m.matchpart2 AS matchpart2 "
			+ "JOIN m.project AS project "
			+ "where project.id = :project_id and m.published = :published and ( matchpart1.id = :matchpart1 or matchpart2.id = :matchpart2 ) and m.count_result = :count_result order by m.match_date desc";

	public List<TableRow> getTableRows(Long projectId) {
		java.util.Date startDate = new java.util.Date();
		List<TableRow> result = calculateTable(projectId);
		java.util.Date date = new java.util.Date();
//		System.out.println("Point A1: " + (date.getTime() - startDate.getTime()));
		for (TableRow row : result) {
			row.setLastMatches(getLastMatches(projectId, row.getTeam_id()));
		}
		date = new java.util.Date();
//		System.out.println("Point A2: " + (date.getTime() - startDate.getTime()));
		return result;
	}

	private List<TableRow> calculateTable(Long projectId) {
		java.util.Date startDate = new java.util.Date();
		List<Team> allTeams = teamLeagueDAO.getTeams(projectId);
		java.util.Date date = new java.util.Date();
//		System.out.println("Point B1: " + (date.getTime() - startDate.getTime()));
		List<Long> allTeamsIds = new ArrayList<Long>();
		for (Team t : allTeams) {
			allTeamsIds.add(t.getId());
		}
		List<TableRow> firstResult = calculateTable(projectId, allTeamsIds);
		int i = 0;
		for (TableRow tr1 : firstResult) {
			tr1.setSequence(i++);
		}
		date = new java.util.Date();
//		System.out.println("Point B2: " + (date.getTime() - startDate.getTime()));
		List<TableRow> result = new ArrayList<TableRow>();
		Map<Long, TableRow> equalPoints = new HashMap<Long, TableRow>();
		Integer points = Integer.MIN_VALUE;
		for (TableRow row : firstResult) {
			if (equalPoints.isEmpty()) {
				equalPoints.put(row.getTeam_id(), row);
				points = row.getPoints();
			} else if (row.getPoints().equals(points)) {
				equalPoints.put(row.getTeam_id(), row);
			} else if (equalPoints.size() == 1) {
				for (TableRow r : equalPoints.values()) {
					result.add(r);
				}
				equalPoints.clear();
				equalPoints.put(row.getTeam_id(), row);
				points = row.getPoints();
			} else {
				result.addAll(sortEqualPoints(projectId, equalPoints));
				equalPoints.clear();
				equalPoints.put(row.getTeam_id(), row);
				points = row.getPoints();
			}
		}
		date = new java.util.Date();
//		System.out.println("Point B3: " + (date.getTime() - startDate.getTime()));
		if (equalPoints.size() == 1) {
			for (TableRow r : equalPoints.values()) {
				result.add(r);
			}
		} else if (equalPoints.size() > 1) {
			result.addAll(sortEqualPoints(projectId, equalPoints));
		}
		date = new java.util.Date();
//		System.out.println("Point B4: " + (date.getTime() - startDate.getTime()));
		return result;
	}

	private List<TableRow> sortEqualPoints(Long projectId, Map<Long, TableRow> equalPoints) {
		List<TableRow> result = new ArrayList<TableRow>();
		List<Long> equalTeams = new ArrayList<Long>(equalPoints.keySet());
		List<Integer> sequence = new ArrayList<Integer>();
		for (Long id : equalTeams) {
			sequence.add(equalPoints.get(id).getSequence());
		}
		List<TableRow> equalTeamResult = calculateTable(projectId, equalTeams, sequence);

		if (equalTeamResult.size() == 2) {
			TableRow r1 = equalTeamResult.get(0);
			TableRow r2 = equalTeamResult.get(1);
			if (r1.getPoints() == r2.getPoints() && r1.getGoalsScoredAway() < r2.getGoalsScoredAway()) {
				result.add(equalPoints.get(r2.getTeam_id()));
				result.add(equalPoints.get(r1.getTeam_id()));
			} else {
				for (TableRow tr : equalTeamResult) {
					result.add(equalPoints.get(tr.getTeam_id()));
				}
			}
		} else {
			for (TableRow tr : equalTeamResult) {
				result.add(equalPoints.get(tr.getTeam_id()));
			}
		}
		return result;
	}

	private List<TableRow> calculateTable(Long projectId, List<Long> teamIds) {
		return calculateTable(projectId, teamIds, null);
	}

	@SuppressWarnings("unchecked")
	private List<TableRow> calculateTable(Long projectId, List<Long> teamIds, List<Integer> sequence) {
		Map<Long, TableRow> result = new HashMap<Long, TableRow>();
		Session session = getCurrentSession();
		for (int i = 0; i < teamIds.size(); i++) {
			Long id = teamIds.get(i);
			TableRow row = new TableRow();
			if (sequence == null) {
				initRow(row, id);
			} else {
				initRow(row, id, sequence.get(i));
			}
			result.put(id, row);
		}

		Query query = session.createQuery(matchHomeQuery);
		query.setParameter("project_id", projectId);
		query.setParameter("published", true);
		query.setParameter("count_result", true);
		query.setParameterList("team_ids", teamIds);

		List<Object[]> rows = query.list();
		for (Object[] r : rows) {
			Long teamId = (Long) r[0];
			if (result.containsKey(teamId)) {
				TableRow row = result.get(teamId);
				addValues(row, r);
				addValuesHome(row, r);
				result.put(teamId, row);
			}
		}

		query = session.createQuery(matchAwayQuery);
		query.setParameter("project_id", projectId);
		query.setParameter("published", true);
		query.setParameter("count_result", true);
		query.setParameterList("team_ids", teamIds);
		rows = query.list();
		for (Object[] r : rows) {
			Long teamId = (Long) r[0];
			if (result.containsKey(teamId)) {
				TableRow row = result.get(teamId);
				addValues(row, r);
				addValuesAway(row, r);
				result.put(teamId, row);
			}
		}

		List<TableRow> sortedResult = new ArrayList<TableRow>(result.values());
		Collections.sort(sortedResult, new TableRowComparator());
		return sortedResult;
	}

	private TableRowMatch[] getLastMatches(Long project, Long teamId) {
		TableRowMatch[] result = new TableRowMatch[NUMBER_OF_LAST_MATCHES];
		Session session = getCurrentSession();
		Query query = session.createQuery(lastMatchesQuery);
		query.setParameter("project_id", project);
		query.setParameter("published", true);
		query.setParameter("matchpart1", teamId);
		query.setParameter("matchpart2", teamId);
		query.setParameter("count_result", true);
		query.setMaxResults(NUMBER_OF_LAST_MATCHES);
		@SuppressWarnings("unchecked")
		List<Object[]> matches = query.list();
		int i = 0;
		for (Object[] match : matches) {
			TableRowMatch m = new TableRowMatch();
			Timestamp ts = (Timestamp) match[0];
			Float result1 = (Float) match[1];
			Float result2 = (Float) match[2];
			String team1 = (String) match[3];
			String team2 = (String) match[4];
			Long team1Id = (Long) match[5];
			if (ts != null) {
				m.setDate(new Date(ts.getTime()));
			}
			String rivalName = teamId.equals(team1Id) ? team2 : team1;
			m.setResult(Math.round(teamId.equals(team1Id) ? result1 - result2 : result2 - result1));
			m.setDescription(Math.round(result1) + "-" + Math.round(result2) + " przeciwko " + rivalName);
			result[i++] = m;
		}

		return result;
	}

	private void initRow(TableRow row, Long id) {
		initRow(row, id, 0);
	}

	private void initRow(TableRow row, Long id, Integer sequence) {
		row.setSequence(sequence);
		row.setTeam_id(id);
		row.setTeamName("");
		row.setGames(0);
		row.setGoalsScored(0);
		row.setGoalsAgainst(0);
		row.setWins(0);
		row.setDraws(0);
		row.setDefeats(0);
		row.setPoints(0);
		row.setGoalsAgainstHome(0);
		row.setGoalsAgainstAway(0);
		row.setGoalsScoredHome(0);
		row.setGoalsScoredAway(0);
	}

	// private void setValues(TableRow row, Object[] r) {
	// row.setTeamName((String) r[1]);
	// row.setGames(((BigInteger) r[2]).intValue());
	// row.setGoalsScored(((Double) r[3]).intValue());
	// row.setGoalsAgainst(((Double) r[4]).intValue());
	// row.setWins(((BigDecimal) r[5]).intValue());
	// row.setDraws(((BigDecimal) r[6]).intValue());
	// row.setDefeats(((BigDecimal) r[7]).intValue());
	// row.setPoints(row.getWins() * 3 + row.getDraws());
	// }

	private void addValues(TableRow row, Object[] r) {
		row.setTeamName((String) r[1]);
		row.setGames(((Long) r[2]).intValue() + row.getGames());
		row.setGoalsScored(((Double) r[3]).intValue() + row.getGoalsScored());
		row.setGoalsAgainst(((Double) r[4]).intValue() + row.getGoalsAgainst());
		row.setWins(((Long) r[5]).intValue() + row.getWins());
		row.setDraws(((Long) r[6]).intValue() + row.getDraws());
		row.setDefeats(((Long) r[7]).intValue() + row.getDefeats());
		row.setPoints(row.getWins() * 3 + row.getDraws());
	}

	private void addValuesHome(TableRow row, Object[] r) {
		row.setGoalsScoredHome(((Double) r[3]).intValue() + row.getGoalsScoredHome());
		row.setGoalsAgainstHome(((Double) r[4]).intValue() + row.getGoalsAgainstHome());
	}

	private void addValuesAway(TableRow row, Object[] r) {
		row.setGoalsScoredAway(((Double) r[3]).intValue() + row.getGoalsScoredAway());
		row.setGoalsAgainstAway(((Double) r[4]).intValue() + row.getGoalsAgainstAway());
	}

	private class TableRowComparator implements Comparator<TableRow> {

		@Override
		public int compare(TableRow tr1, TableRow tr2) {
			if (tr1.getPoints() > tr2.getPoints()) {
				return -1;
			} else if (tr1.getPoints() < tr2.getPoints()) {
				return 1;
			} else if (tr1.getGoalsScored() - tr1.getGoalsAgainst() > tr2.getGoalsScored() - tr2.getGoalsAgainst()) {
				return -1;
			} else if (tr1.getGoalsScored() - tr1.getGoalsAgainst() < tr2.getGoalsScored() - tr2.getGoalsAgainst()) {
				return 1;
			} else if (tr1.getGoalsScored() > tr2.getGoalsScored()) {
				return -1;
			} else if (tr1.getGoalsScored() < tr2.getGoalsScored()) {
				return 1;
			} else if (tr1.getSequence() > tr2.getSequence()) {
				return 1;
			} else if (tr1.getSequence() < tr2.getSequence()) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
