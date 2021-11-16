let mail = document.getElementById("mail").value;
let password= document.getElementById("password").value;
let confirmPassword = document.getElementById("confirmPassword").value;
let lastName = document.getElementById("lastName").value;
let firstName = document.getElementById("firstName").value;

Vue.component("errors", {
	props: ["errors"],
	template: `
		<div class="errors" v-if="errors">
			<div class="text-danger" v-for="error in errors">{{ error }}</div>
		</div>
	`
})

const app = new Vue ({
	el: "#app",
	data: {
		errors: {
			mail: [],
			password: [],
			confirmPassword: [],
			lastName: [],
			firstName: []
		},
		mail: mail,
		password: password,
		confirmPassword: confirmPassword,
		lastName: lastName,
		firstName: firstName
	},
	methods: {
		checkForm: function(e) {
			let errors = {
					mail: [],
					password: [],
					confirmPassword: [],
					lastName: [],
					firstName: []
			};

			if (!this.mail) errors.mail.push("メールアドレスを入力してください");
			else if (this.mail.length > 100) errors.password.push("メールアドレスは100文字以下で入力してください");

			if (!this.password) errors.password.push("パスワードを入力してください");
			else {
				if (this.password.length < 8 || this.password.length > 64) errors.password.push("パスワードは8文字以上、64文字以下で入力してください");
				if (!this.password.match(/^[a-zA-Z0-9]+$/)) errors.password.push("パスワードに使用できない文字が含まれています");
			}

			if (!this.confirmPassword) errors.confirmPassword.push("パスワードを再度入力してください");
			else if (this.password != this.confirmPassword) errors.confirmPassword.push("パスワードと確認用パスワードが一致しません");

			if (!this.lastName) errors.lastName.push("名字を入力してください");
			else if (this.lastName > 30) errors.lastName.push("名字は30文字以下で入力してください");
			if (!this.firstName) errors.firstName.push("名前を入力してください");
			else if (this.firstName > 30) errors.firstName.push("名前は30文字以下で入力してください");

			this.errors = errors;

			for (let key in errors) {
				if (errors[key].length > 0) {
					e.preventDefault();
					return;
				}
			}
		}
	}
})