document.addEventListener("DOMContentLoaded", function() {
	const passwordInput = document.getElementById('password');
	const emailInput = document.getElementById('email');
	const showPasswordButton = document.getElementById('showpassword');
	const dobInput = document.getElementById('dob');
	const dobError = document.getElementById('dobError');
	const form = document.getElementById('registrationForm');
	const nameInput = document.getElementById('name');
	const pancardnoInput = document.getElementById('pancardno');
	const phoneInput = document.getElementById('phone');
	const fileInput = document.getElementById('file-upload');
	const fileNameDisplay = document.getElementById('file-name');

	const errorDiv = document.querySelector('.error');
	if (errorDiv) {
		// Play the sound
		var audio = document.getElementById('errorSound');
		audio.play();
	}

	showPasswordButton.addEventListener('click', function() {
		const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
		passwordInput.setAttribute('type', type);
		this.classList.toggle('uil-eye');
		this.classList.toggle('uil-eye-slash');
	});

	emailInput.addEventListener('input', function() {
		validateField(this, /^[a-z0-9+_.-]+@[a-z0-9.-]+\.[a-z]{2,}$/, "Please enter a valid email like : user@gmail.com");
	});

	passwordInput.addEventListener('input', function() {
		validateField(this, /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[#@$!%*?&])[A-Za-z\d@$!#%*?&]{6,}$/,
			"Must contain A-z,a-z,0-9,special character and at least 6 characters long ");
	});

	dobInput.addEventListener('change', function() {
		validateDOB();
	});

	nameInput.addEventListener('input', function() {
		validateField(this, /^[A-Za-z ]+$/, "Please enter a valid name (letters only)");
	});

	pancardnoInput.addEventListener('input', function() {
		validateField(this, /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/, "Must be in this form (5 uppercase letters, 4 digits, 1 uppercase letter) like ABCDE1234A ");
	});

	phoneInput.addEventListener('input', function() {
		validateField(this, /^[6-9]\d{9}$/, "Please enter a valid 10-digit phone number ");
	});

	fileInput.addEventListener('change', function() {
		const fileName = fileInput.files.length > 0 ? fileInput.files[0].name : 'No file chosen';
		fileNameDisplay.textContent = fileName;
	});


	form.addEventListener('submit', function(event) {
		if (!validateDOB() || !validateAllFields()) {
			event.preventDefault();
		}
	});

	function validateDOB() {
		const dobValue = dobInput.value;
		const dobDate = new Date(dobValue);
		const today = new Date();

		let age = today.getFullYear() - dobDate.getFullYear();
		const monthDifference = today.getMonth() - dobDate.getMonth();
		const dayDifference = today.getDate() - dobDate.getDate();

		if (monthDifference < 0 || (monthDifference === 0 && dayDifference < 0)) {
			age--;
		}

		if (age < 18) {
			dobError.style.display = 'block';
			dobError.textContent = 'You must be at least 18 years old.';
			return false;
		} else {
			dobError.style.display = 'none';
			return true;
		}
	}

	function validateField(field, regex, errorMessage) {
		const errorSpan = document.getElementById(`${field.id}Error`);
		if (!regex.test(field.value)) {
			errorSpan.style.display = 'block';
			errorSpan.textContent = errorMessage;
			return false;
		} else {
			errorSpan.style.display = 'none';
			return true;
		}
	}

	function validateAllFields() {
		return validateField(nameInput, /^[A-Za-z ]+$/, "Please enter a valid name (letters only)") &&
			validateField(emailInput, /^[a-z0-9+_.-]+@[a-z0-9.-]+\.[a-z]{2,}$/, "Please enter a valid email address") &&
			validateField(pancardnoInput, /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/, "Must be in this form (5 uppercase letters, 4 digits, 1 uppercase letter)") &&
			validateField(phoneInput, /^[6-9][0-9]{9}$/, "Please enter a valid 10-digit phone number") &&
			validateDOB();
	}



});

