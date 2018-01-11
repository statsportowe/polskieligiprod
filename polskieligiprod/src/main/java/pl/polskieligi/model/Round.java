package pl.polskieligi.model;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Round {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	  private Long id;
	  private Integer matchcode;
	  private String name;
	  private Date round_date_first;
	  private Date round_date_last;
	  @ManyToOne
	  private Project project;
	  private Integer checked_out;
	  private Timestamp checked_out_time;
	  public Round(){
		  matchcode = 0;
		  name = "";
		  round_date_first = new Date(0);
		  round_date_last = new Date(0);
		  checked_out = 0;
		  checked_out_time = new Timestamp(0);
	  }
	  
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getMatchcode() {
		return matchcode;
	}
	public void setMatchcode(Integer matchcode) {
		this.matchcode = matchcode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getRound_date_first() {
		return round_date_first;
	}
	public void setRound_date_first(Date round_date_first) {
		this.round_date_first = round_date_first;
	}
	public Date getRound_date_last() {
		return round_date_last;
	}
	public void setRound_date_last(Date round_date_last) {
		this.round_date_last = round_date_last;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
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
	
}
