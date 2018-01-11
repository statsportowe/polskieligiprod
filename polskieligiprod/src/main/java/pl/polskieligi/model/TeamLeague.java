package pl.polskieligi.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TeamLeague {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private Project project;
	@ManyToOne
	private Team team;
	private Integer division_id;
	private Integer start_points;
	private Integer admin;
	private String info;
	private Integer checked_out;
	private Timestamp checked_out_time;
	private String picture;
	private String description;
	private Integer standard_playground;

	public TeamLeague() {
		division_id = 0;
		start_points = 0;
		admin = 0;
		info = "";
		checked_out = 0;
		checked_out_time = new Timestamp(0);
		picture = "";
		description = "";
		standard_playground = 0;
	}

	public Integer getDivision_id() {
		return division_id;
	}

	public void setDivision_id(Integer division_id) {
		this.division_id = division_id;
	}

	public Integer getStart_points() {
		return start_points;
	}

	public void setStart_points(Integer start_points) {
		this.start_points = start_points;
	}

	public Integer getAdmin() {
		return admin;
	}

	public void setAdmin(Integer admin) {
		this.admin = admin;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Integer getChecked_out() {
		return checked_out;
	}

	public void setChecked_out(Integer checked_out) {
		this.checked_out = checked_out;
	}

	public Timestamp getChecked_out_time() {
		return checked_out_time;
	}

	public void setChecked_out_time(Timestamp checked_out_time) {
		this.checked_out_time = checked_out_time;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getStandard_playground() {
		return standard_playground;
	}

	public void setStandard_playground(Integer standard_playground) {
		this.standard_playground = standard_playground;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
}
