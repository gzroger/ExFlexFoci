package com.gzroger.exflexfoci;

public class Player {

	String stName;
	int monBalance;
	static final String TABN = "player";
	
	public Player(String stName) {
		this.stName = stName;
	}

	public String stNameGet() {
		return stName;
	}
	
	public int monBalanceGet() {
		return monBalance;
	}
	
	public void setMonBalance(int monBalance) {
		this.monBalance = monBalance;
	}

	@Override
	public String toString() {
		return "Player [stName=" + stName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((stName == null) ? 0 : stName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (stName == null) {
			if (other.stName != null)
				return false;
		} else if (!stName.equals(other.stName))
			return false;
		return true;
	}
	

}