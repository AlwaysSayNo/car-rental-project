findPlaceholder = (className, subject) => {
    let next = subject.nextElementSibling;
    let placeholder;

    while (next) {
        if (next.classList.contains(className)) {
            placeholder = next;
            break;
        }

        next = next.nextElementSibling;
    }

    return placeholder
}

getErrorsPlace = (element) => {
    return findPlaceholder('error', element);
}


showErrorMessage = (element, message) => {
    element.textContent = message;
    element.className = 'error active';
}

clearError = (element) => {
    element.textContent = '';
    element.className = 'error';
}


onStart = (document, placeholders) => {
    document.addEventListener('DOMContentLoaded', (event) => {
        placeholders.forEach((input, flag) => {
            if (flag) {
                showErrorMessage(getErrorsPlace(input), getErrorMessage(input));
                flag = false;
            }
        });
    });
}

onInput = (element, getMessageFunc) => {
    element.addEventListener('input', function (event) {
        if (element.validity.valid) clearError(getErrorsPlace(element));
        else showErrorMessage(getErrorsPlace(element), getMessageFunc(element));
    })
}

onSubmit = (form, elementsArr, getMessageFunc) => {
    form.addEventListener('submit', function (event) {
        let hasException = false;
        for(let i = 0; i < elementsArr.length; ++i){
            let element = elementsArr[i];
            if(!element.disabled && !element.validity.valid) {
                hasException = true;
                showErrorMessage(getErrorsPlace(element), getMessageFunc(element));
            }
            else{
                clearError(getErrorsPlace(element));
            }
        }
        if(hasException) event.preventDefault();
    });
}

getErrorMessage = (element) => {
    let message = null;

    if(element === externalError){
        if(invalidCredentialsException)
            message =  /*[[#{invalid.credentials.exception}]]*/ 'There is no email with this password.';
    } else {
        if (element.validity.valueMissing)
            message = /*[[#{required.exception}]]*/ 'This field is required.';
        else if (element.validity.typeMismatch)
            message = /*[[#{invalid.type.error}]]*/ 'This field must match its type.';
        else if (element.validity.patternMismatch)
            message = /*[[#{invalid.structure.error}]]*/ 'This field must match its structure.';
        else if (element.validity.rangeUnderflow || element.validity.rangeOverflow)
            message = /*[[#{invalid.range.error}]]*/ 'Incorrect range of field';
    }

    return message;
}
