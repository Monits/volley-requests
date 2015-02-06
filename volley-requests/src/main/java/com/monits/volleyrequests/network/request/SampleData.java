package com.monits.volleyrequests.network.request;

public class SampleData {
	public String data;

	public SampleData(final String data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SampleData)) return false;

		SampleData that = (SampleData) o;

		return !(data != null ? !data.equals(that.data) : that.data != null);
	}

	@Override
	public int hashCode() {
		return data != null ? data.hashCode() : 0;
	}
}
