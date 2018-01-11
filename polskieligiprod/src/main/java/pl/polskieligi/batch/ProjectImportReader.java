package pl.polskieligi.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class ProjectImportReader implements ItemReader<Long> {
	private Long start = null;
	private Long end = null;

	private Long index = null;

	@Override
	public Long read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(index==null) {
			index=new Long(start);
		}
		if (index <= end) {
			return index++;
		}
		return null;
	}

	public void setStart(final Long start) {
		this.start = start;
	}

	public void setEnd(final Long end) {
		this.end = end;
	}
}
