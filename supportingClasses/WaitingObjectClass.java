package gov.nasa.jpf.supportingClasses;

public class WaitingObjectClass {
	
	private static final int SENDER = 0 ;
	private static final int RECEIVER = 1 ;
	
	public boolean used ;
	public int added_by ;
	public boolean flag = true ;
	
	public int object_added_by ;
	public int sender ;
	public int receiver ;
	public int tag ;
	public Message msg ;
	
	WaitingObjectClass(int _sender, int _receiver, int _tag, int _added_by, Message _msg) {
		set_sender(_sender) ;
		set_receiver(_receiver) ;
		set_tag(_tag) ;
		if(_added_by == 0)
			set_added_by(1) ;
		else
			set_added_by(0) ;
		set_message(_msg) ;
		set_used(false) ;
	}
	
	public boolean get_used() {
		return this.used ;
	}
	
	public void set_used(boolean _used) {
		this.used = _used ;
	}
	
	public int get_added_by() {
		return added_by ;
	}
	
	public void set_added_by(int _added_by) {
		this.added_by = _added_by ;
	}
	
	public boolean get_flag() {
		return flag ;
	}
	
	public void set_flag(boolean _flag) {
		this.flag = _flag ;
	}

	public int get_object_added_by() {
		return object_added_by ;
	}
	
	public void set_object_added_by(int _object_added_by) {
		this.object_added_by = _object_added_by ;
	}
	
	public int get_sender() {
		return this.sender ;
	}
	
	public void set_sender(int _sender) {
		this.sender = _sender ;
	}
	
	public int get_receiver() {
		return this.receiver ;
	}
	
	public void set_receiver(int _receiver) {
		this.receiver = _receiver ;
	}
	
	public int get_tag() {
		return this.receiver ;
	}
	
	public void set_tag(int _tag) {
		this.tag = _tag ;
	}
	
	public Message get_message() {
		return this.msg ;
	}
	
	public void set_message(Message _msg) {
		this.msg = _msg ;
	}
}
