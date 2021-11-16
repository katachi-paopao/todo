let workRegister = new Vue ({
	el: "#work-register",
	data: {
		workerList: [],
		name: null,
		selected: [],
		errors: {
			name: [],
			selected: []
		}
	},
	methods: {
		fullName({lastName, firstName}) {
			return lastName + " " + firstName;
		},
		checkForm: function(e) {
			let errors = {
				name: [],
				selected: []
			};

			if (!this.name) errors.name.push("作業名を入力してください");
			else if (this.name > 50) errors.name.push("作業名は50文字以下で入力してください");
			if (this.selected.length == 0) errors.selected.push("担当者は1人以上登録してください");

			this.errors = errors;

			for (let key in errors) {
				if (errors[key].length > 0) {
					e.preventDefault();
					return;
				}
			}
		},
		edit() {
			this.editMode = true;
		}
	},
	mounted: function() {
		let self = this;
		axios.get("http://localhost:8080/todo/rest/getUserList")
		.then(function(response){ self.workerList = response.data });
	}
});