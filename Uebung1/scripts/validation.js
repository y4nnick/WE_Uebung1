
$(function () {
    var $fname = $("#fname");
    var $lname = $("#lname");
    var $bday = $("#bday");
    var $password = $("#password");
    var $email = $("#email");
    var $AGB = $("#AGB");
    var fields = [$fname, $lname, $bday, $password, $email];


    function isMinAge(normalizedBirthday) {
        var minAge = 18;
        var tempDate = new Date(normalizedBirthday.getFullYear() + minAge, normalizedBirthday.getMonth(), normalizedBirthday.getDate());
        return (tempDate <= new Date());
    }
    function isValidDate(normalizedBirthday) {
        return !isNaN(normalizedBirthday.getTime());
    }

    if(!hasNativeDateInput() || !hasFormValidation()) {
        var validateBirthday = function () {
            var normalizedDate = getNormalizedDateString("#bday").split(".");
            var normalizedBirthday = new Date(normalizedDate[2], normalizedDate[1] - 1, normalizedDate[0]);
            var elem = $(this);

            elem.find("~ .error-invalid-date").hide();
            elem.find("~ .error-not-born-yet").hide();
            elem.find("~ .error-too-young").hide();
            elem.data('valid', true);

            if (!isValidDate(normalizedBirthday)) {
                elem.find("~ .error-invalid-date").show();
                elem.data('valid', false);
            } else if (normalizedBirthday > new Date()) {
                elem.find("~ .error-not-born-yet").show();
                elem.data('valid', false);
            } else if (!isMinAge(normalizedBirthday)) {
                elem.find("~ .error-too-young").show();
                elem.data('valid', false);
            }

            if (hasFormValidation()) {
                if (elem.data('valid')) {
                    elem.setCustomValidity();
                } else {
                    elem.setCustomValidity("Sie müssen mindestens 18 Jahre alt sein.");
                }
            } else {
                checkValidForm();
            }
        };

        $bday.on('focus keyup change', validateBirthday);
    }

    if(!hasFormValidation()) {
        var $submitButton = $("#submitButton");
        $submitButton.attr('disabled', true);

        for (var field in fields) {
            fields[field].data('valid', false);
        }

        function checkValidForm() {
            for (var field in fields) {
                if (!fields[field].data('valid')) {
                    $submitButton.attr('disabled', true);
                    return;
                }
            }

            $submitButton.removeAttr('disabled');
        }

        var validateMandatory = function () {
            var elem = $(this);
            var val = elem.val();

            elem.find("~ .error-mandatory").hide();
            elem.data('valid', true);

            if (val == null || val == "") {
                elem.find("~ .error-mandatory").show();
                elem.data('valid', false);
            }

            checkValidForm();
        };

        $fname.on('focus keyup change', validateMandatory);
        $lname.on('focus keyup change', validateMandatory);
        $bday.on('focus keyup change', validateMandatory);
        $password.on('focus keyup change', validateMandatory);
        $email.on('focus keyup change', validateMandatory);

        var validateEmail = function () {
            var elem = $(this);
            var regex = /^\S+@\S+\.\S+$/;

            elem.find("~ .error-invalid").hide();
            elem.data('valid', true);

            if (!regex.test(elem.val())) {
                elem.find("~ .error-invalid").show();
                elem.data('valid', false);
            }

            checkValidForm();
        };

        $email.on('focus keyup change', validateEmail);

        var validatePassword = function () {
            var elem = $(this);
            var val = elem.val();

            elem.find("~ .error-too-short").hide();
            elem.find("~ .error-too-long").hide();
            elem.data('valid', true);

            if (val.length < 4 && val.length != 0) {
                elem.find("~ .error-too-short").show();
                elem.data('valid', false);
            } else if (val.length > 8) {
                elem.find("~ .error-too-long").show();
                elem.data('valid', false);
            } else if (val.length == 0) {
                elem.data('valid', false);
            }

            checkValidForm();
        };

        $password.on('focus keyup change', validatePassword);

        var validateAGB = function () {
            var val = $AGB.is(':checked');

            $AGB.find("~.error-not-checked").hide();

            if (val == false) {
                $AGB.find("~.error-not-checked").show();
                return false;
            }
        };

        $submitButton.on('click', function () {
            var validAGB = validateAGB();
            if (validAGB) {
                alert('Vielen Dank!');
            }
        });
    }
});
