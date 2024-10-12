$(document).ready(function() {
  // Edit Nominee
  $('.btnEditNominee').on('click', function() {
    var nomineeId = $(this).data('id');
    var nomineeName = $(this).data('name');
    var relationship = $(this).data('relationship');
    var phone = $(this).data('phone');

    $('#editNomineeId').val(nomineeId);
    $('#editNomineeName').val(nomineeName);
    $('#editRelationship').val(relationship);
    $('#editPhone').val(phone);

    $('#editNomineePopup').show();
  });

  $('#btnCloseEditNomineeForm').on('click', function() {
    $('#editNomineePopup').hide();
  });

  // Delete Nominee
  $('.btnDeleteNominee').on('click', function() {
    var nomineeId = $(this).data('id');
    $('#deleteNomineeId').val(nomineeId);
    $('#deleteNomineePopup').show();
  });

  $('#btnCloseDeleteNomineeForm').on('click', function() {
    $('#deleteNomineePopup').hide();
  });

  $('#btnCancelDeleteNominee').on('click', function() {
    $('#deleteNomineePopup').hide();
  });

  // Close button functionality
  $('#btnCloseAddMoneyForm').on('click', function() {
    $('#addMoneyPopup').hide();
  });

  // Add Money Form
  $('#btnOpenAddMoneyForm').on('click', function() {
    $('#addMoneyPopup').show();
  });

  // Profile Picture
  $('#btnUpdateProfilePicture').on('click', function() {
    $('#updateProfilePicturePopup').show();
  });

  $('#btnCloseUpdateProfilePicture').on('click', function() {
    $('#updateProfilePicturePopup').hide();
  });

  // Add Nominee Form
  $('#btnOpenNomineeForm').on('click', function() {
    $('#addNomineePopup').show();
  });

  $('#btnCloseNomineeForm').on('click', function() {
    $('#addNomineePopup').hide();
  });

  // Form Submission Handling for Add Money Form
  $('#addMoneyForm').on('submit', function(e) {
    const expiry = $('#expiry').val();
    if (!isExpiryDateValid(expiry)) {
      alert('Expiry date must be a valid future date.');
      e.preventDefault(); // Prevent form submission
      $('#expiry').addClass('is-invalid');
      $('#expiry').next('.invalid-feedback').text('Expiry date must be a valid future date.');
    } else {
      $('#expiry').removeClass('is-invalid').addClass('is-valid');
    }
  });

  // Field Validation on Input
  $('form').on('input', '.form-control', function() {
    validateField(this);
  });

  function validateField(field) {
    // Clear previous validation feedback
    $(field).removeClass('is-invalid').removeClass('is-valid');

    // Check if the field is valid
    if (field.checkValidity()) {
      $(field).addClass('is-valid');
    } else {
      $(field).addClass('is-invalid');
    }
  }

  // Function to handle form submission
  $('form').on('submit', function(e) {
    var isValid = true;

    // Validate all form controls
    $(this).find('.form-control').each(function() {
      if (!this.checkValidity()) {
        isValid = false;
        validateField(this); // Show error for invalid fields
      }
    });

    // Prevent form submission if there are errors
    if (!isValid) {
      e.preventDefault();
      $(this).addClass('was-validated'); // Show feedback for all fields
    }
  });

  function isExpiryDateValid(expiry) {
    if (!expiry) return false; // Handle empty expiry input

    const [month, year] = expiry.split('/').map(str => parseInt(str, 10));
    if (isNaN(month) || isNaN(year) || month < 1 || month > 12 || year < 1000) {
        return false; // Invalid month/year format
    }

    const currentDate = new Date();
    const expiryDate = new Date(year, month); // Using year and month directly

    // Ensure expiry date is strictly in the future
    return expiryDate > currentDate;
  }
});
