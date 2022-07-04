onStart = (placeholders) => {
    placeholders.forEach((input, flag) => {
        if (flag) {
            showErrorMessage(getErrorsPlace(input), getErrorMessage(input));
            flag = false;
        }
    });
}

onInput = (element, getMessageFunc) => {
    if (element.validity.valid) clearError(getErrorsPlace(element));
    else showErrorMessage(getErrorsPlace(element), getMessageFunc(element));
}

onSubmit = (elementsArr, getMessageFunc) => {
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

    return hasException;
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

countErrors = (elements) => {
    let count = 0;
    elements.forEach (function (element){
        if(!element.validity.valid) count += 1;
    });
    return count;
}