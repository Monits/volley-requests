/**
 * Copyright 2010 - 2015 Monits
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.monits.volleyrequests.network.request;

public class SampleData {
	public String data;

	public SampleData(final String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "SampleData{"
				+ "data='" + data + '\''
				+ '}';
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
