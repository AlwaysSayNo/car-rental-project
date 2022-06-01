const toggleFork = (id) => {
    const textField = document.querySelectorAll("#" + id + ' input[type="text"]')[0];
    textField.disabled = !textField.disabled;
    const selector = document.querySelectorAll("#" + id + " select")[0];
    selector.disabled = !selector.disabled;
}

const form  = document.getElementsByTagName('form')[0];
const newCarBrandField = document.getElementById('new-car-brand');
const carName = document.getElementById('car-name');
const carNumber = document.getElementById('car-number');
const newCarColor = document.getElementById('add-new-car-color');
const carPricePerDay = document.getElementById('price-per-day');

carNumberAlreadyExistsError = "[[${carNumberAlreadyExistsError}]]" === "true";

carNumber.setAttribute("pattern", "[[#{car.number.regexp}]]" /*"\p{Upper}{2} \d{4} \p{Upper}{2}"*/);

getErrorMessage = (element) => {
    let message;
    if(element === carNumber && carNumberAlreadyExistsError){
        message = "[[#{error.car.number.already.exists}]]" /*Car with such number already exists*/
        carNumberAlreadyExistsError = false;
    }
    else if(element.validity.valueMissing) {
        message = "[[#{error.required}]]" /*'This field is required'*/;
    }
    else if(element.validity.typeMismatch) {
        message = "[[#{error.incorrect.type}]]" /*'This field must match its type: ' + element.type*/;
    }
    else if(element.validity.patternMismatch) {
        message = "[[#{error.incorrect.pattern}]]" /*'This field must match its pattern: ' + element.pattern*/;
    }
    else if(element.validity.rangeUnderflow || element.validity.rangeOverflow) {
        message = "[[#{error.incorrect.range}]]" /*'Incorrect range: min ' + element.min + ', max ' + element.max*/;
    }
    return message;
}

const elementsArr = [newCarBrandField, carName, carNumber, newCarColor, carPricePerDay];

document.addEventListener('DOMContentLoaded', (event) => {
    if(carNumberAlreadyExistsError === true){
        showErrorMessage(carNumber, getErrorMessage(carNumber));
    }
});


elementsArr.forEach(element => element.addEventListener('input', function (event) {
        if (element.validity.valid) clearError(element);
        else showErrorMessage(element, getErrorMessage(element));
    })
);

form.addEventListener('submit', function (event) {
    let hasException = false;
    for(let i = 0; i < elementsArr.length; ++i){
        let element = elementsArr[i];
        if(!element.disabled && !element.validity.valid) {
            hasException = true;
            let message = getErrorMessage(element);
            showErrorMessage(element, message);
        }
        else{
            clearError(element);
        }
    }
    if(hasException) event.preventDefault();
});

showErrorMessage = (element, message) => {
    let errorSpan = element.nextElementSibling;
    errorSpan.textContent = message;
    errorSpan.className = 'error active';
}

clearError = (element) => {
    let errorSpan = element.nextElementSibling;
    errorSpan.textContent = '';
    errorSpan.className = 'error';
}