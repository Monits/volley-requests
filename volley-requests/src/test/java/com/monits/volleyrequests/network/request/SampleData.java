package com.monits.volleyrequests.network.request;

public class SampleData {
	public String data;

	public SampleData(final String data) {
		this.data = data;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SampleData)) {
			return false;
		}

		final SampleData that = (SampleData) o;

		return data == null ? that.data == null : data.equals(that.data);
	}

	@Override
	public int hashCode() {
		return data == null ? 0 : data.hashCode() ;
	}
}
