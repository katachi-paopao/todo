Vue.component("multiselect", window.VueMultiselect.default);

Vue.component("errors", {
	props: ["errors"],
	template: `
		<div class="errors" v-if="errors">
			<div class="text-danger" v-for="error in errors">{{ error }}</div>
		</div>
	`
})

let todoList = new Vue ({
	el:	 "#todoContents",
	data: {
		searchWord: "",
		updateStatusTarget: {
			workId: null,
			status: null
		},
		sort: {
			key: "",
			isAsc: true,
			isFilteredByUser: false,
		},
		detailModal: {
			workId: null,
			workDetail: null,
			workStatus: null,
			deadlineDate: "",
			completionDate: "",
			workerList: null,
			editMode: false
		},
		errors: {
			worker: []
		},
		workList: [],
		userList: [],
		user: user,
		statusCodeMap: {},
		csrf: csrfToken
	},
	computed: {
		filteredWorkList: function() {
			let list = this.workList.slice();

			if (this.sort.isFilteredByUser) {
				list = list.filter(work => {
					for(let i = 0; i < work.workerList.length; i++) {
						if (work.workerList[i].lastName.concat(work.workerList[i].firstName).toUpperCase().
							indexOf(this.user.toUpperCase()) > -1) return true;
					}
				});
			}

			if (new RegExp("\\S").test(this.searchWord)) {
				list = list.filter(work => {
					if (work.name.toUpperCase().indexOf(this.searchWord.toUpperCase()) > -1) return true;
					for(let i = 0; i < work.workerList.length; i++) {
						if (work.workerList[i].lastName.concat(work.workerList[i].firstName).toUpperCase().
							indexOf(this.searchWord.toUpperCase()) > -1) return true;
					}
				});
			}

			if (this.sort.key) {
				list.sort((a, b) => {
					a = a[this.sort.key] ? parseInt(a[this.sort.key].replace(/\//g, "")) : this.sort.isAsc ? Number.MAX_VALUE : Number.MIN_VALUE;
					b = b[this.sort.key] ? parseInt(b[this.sort.key].replace(/\//g, "")) : this.sort.isAsc ? Number.MAX_VALUE : Number.MIN_VALUE;
					return (a === b ? 0 : a > b ? 1 : - 1) * (this.sort.isAsc ? 1 : -1);
				})
			}
			return list;
		}
	},
	methods: {
		parseDate: function(date) {
			if (date == null) return;
			return date.substring(0, 10).replace(/-/g, "/");
		},
		isOverdue: function(date) {
			let split_date = date.split("/");
			let d = new Date(parseInt(split_date[0]), parseInt(split_date[1])-1, parseInt(split_date[2])+1);
			let t = new Date();
			return d - t < -1;
		},
		setStatus: function(workId, status){
			this.updateStatusTarget.workId= workId;
			this.updateStatusTarget.status = status;
		},
		updateStatus: function() {
			axios.post("todo/rest/updateStatus", {
				workId: this.updateStatusTarget.workId,
				status: this.updateStatusTarget.status
			}, {
				headers: {
					"X-CSRF-TOKEN": this.csrf
				}
			}).then(response => {
				this.refresh();
			});
		},
		deleteWork: function(workId) {
			if ( confirm("削除すると元に戻せません、削除しますか？" ) ) {
				axios.post("todo/rest/deleteWork", { workId: workId },
				{
					headers: {
						"X-CSRF-TOKEN": this.csrf
					}
				}).then(response => {
					document.getElementById("closeDetail").click();
					this.refresh()
				});
			}
		},
		updateWork: function() {
			let errors = {
				worker: []
			}

			if (this.detailModal.workerList.length == 0) {
				errors.worker.push("担当者は1人以上登録してください");
			}

			this.errors = errors;

			for (let key in errors) {
				if (errors[key].length > 0) {
					return;
				}
			}

			if ( confirm("現在の内容で更新しますか？" ) ) {
				axios.post("todo/rest/updateWork",{
					workId: this.detailModal.workId,
					workDetail: this.detailModal.workDetail,
					workStatus: this.detailModal.workStatus,
					deadlineDate: this.detailModal.deadlineDate,
					completionDate: this.detailModal.completionDate,
					workerList: this.detailModal.workerList
				}, {
					headers: {
						"X-CSRF-TOKEN": this.csrf
					}
				}).then(response => {
					document.getElementById("closeDetail").click();
					this.detailModal.editMode = false;
					this.refresh();
				}).catch(error => {
					console.log(error.response);
				});
			}
		},
		fetchDetail: function(workId) {
			let self = this;
			axios.get("todo/rest/detail", {
				params: {
					workId: workId
				}
			}).then(response => {
				self.detailModal.workId = response.data.id;
				self.detailModal.workDetail = response.data.detail;
				self.detailModal.workStatus = response.data.status;
				if (response.data.deadlineDate != null) {
					self.detailModal.deadlineDate = response.data.deadlineDate.replace(/\//g, "-");
				} else {
					self.detailModal.deadlineDate = "";
				}
				if (response.data.completionDate != null) {
					self.detailModal.completionDate = response.data.completionDate.replace(/\//g, "-");
				} else {
					self.detailModal.completionDate = "";
				}
				self.detailModal.workerList = response.data.workerList;
			});
		},
		sortBy(key) {
			this.sort.key = key;
			this.sort.isAsc = !(this.sort.isAsc)
		},
		sortClass(key) {
			if (this.sort.key == key) {
				return this.sort.isAsc ? "asc" : "desc";
			}
		},
		filterByUser() {
			this.sort.isFilteredByUser = !(this.sort.isFilteredByUser);
		},
		refresh() {
			let self = this;
			axios.get("./todo/rest/getWorkList")
					.then(function(response){ self.workList = response.data });
			axios.get("./todo/rest/getUserList")
					.then(function(response){ self.userList = response.data });
		},
		edit() {
			this.detailModal.editMode = !this.detailModal.editMode;
		},
		fullName({lastName, firstName}) {
			return lastName + " " + firstName;
		}
	},
	mounted: function() {
		this.refresh();
		let self = this;
		axios.get("./todo/rest/getStatusCodeMap")
					.then(function(response){ self.statusCodeMap = response.data });
	}
})