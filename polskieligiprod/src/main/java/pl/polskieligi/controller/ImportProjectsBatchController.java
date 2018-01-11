package pl.polskieligi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ImportProjectsBatchController {
	
	@Autowired
	@Qualifier("pojectImportJobLauncher")
	private JobLauncher launcher;
	
	@Autowired
	@Qualifier("projectImportJob")
	Job job;
	
	@RequestMapping("/importProjectsBatch")
	public ModelAndView importProjectsBatch(Long start, Long end) {

		JobExecution execution = null;
		Map<String,JobParameter> parameters = new HashMap<String,JobParameter>();
		parameters.put("start", new JobParameter(start));
		parameters.put("end", new JobParameter(end));
		try {
			execution = launcher.run(job, new JobParameters(parameters));
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			throw new RuntimeException(e);
		}
		ProjectImportJob pij = new ProjectImportJob();
		pij.setJobExecution(execution);
		pij.setProgress(Long.valueOf(0));
		pij.setProcessingTime(Long.valueOf(0));//TODO
		List<ProjectImportJob> rows = new ArrayList<ProjectImportJob>();
		rows.add(pij);
		ModelAndView mv = new ModelAndView("jobStatus", "rows", rows);
		return mv;
	}
}
