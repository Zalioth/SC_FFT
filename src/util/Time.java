package util;

public class Time {
	
	long time_start, time_end;
	long final_time;
	
	
	public Time(){
		time_start = time_end = final_time = 0;
	}
	
	public void start(){
		time_start = System.currentTimeMillis();
	}
	
	public void pause(){
		this.final_time += System.currentTimeMillis() - this.time_start;
	}
	
	public void stop(){
		time_end = System.currentTimeMillis();
		this.final_time += this.time_end - this.time_start;
	}

	public long getTime_start() {
		return time_start;
	}

	public void setTime_start(long time_start) {
		this.time_start = time_start;
	}

	public long getTime_end() {
		return time_end;
	}

	public void setTime_end(long time_end) {
		this.time_end = time_end;
	}
	
	public long getTime(){
		return this.final_time;
	}
	
	public void clear(){
		time_start = time_end = final_time = 0;
	}

}
