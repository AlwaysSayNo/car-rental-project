submitForm = (form, event, filterSelect, sortSelect) => {
    event.preventDefault();

    const none = 'NONE'
    if(filterSelect.value === none && sortSelect.value === none)
        return;

    let action = form.action;

    const target = new URL(action);
    const params = new URLSearchParams();

    const map = new Map();
    map.set('optgroupLabel', '');
    map.set('optionValue', '');

    if(sortSelect.value !== none){
        getFromOptgroup(sortSelect, map);

        params.set('sortBy', map.get('optgroupLabel'));
        params.set('direction', map.get('optionValue'));
    }

    if(filterSelect.value !== none){
        getFromOptgroup(filterSelect, map);

        params.set('filterBy', map.get('optgroupLabel'));
        params.set('filterValue', map.get('optionValue'));
    }

    target.search = params.toString();

    const request = new XMLHttpRequest();
    request.open(form.method, target.href, true);
    // request.onload = function (){
    //     document.replaceChild(request.response);
    // }
    request.send();
    form.submit();
    //window.location.reload();
}

getFromOptgroup = (select, map) => {
    const sortChildren = Array.prototype.slice.call(select.children);

    sortChildren.forEach(function (optgroup){
        if(optgroup.tagName.toLowerCase() === 'optgroup'){
            const options = Array.prototype.slice.call(optgroup.children);

            options.forEach(function (option){
                if(option.selected){
                    map.set('optgroupLabel', optgroup.label);
                    map.set('optionValue', option.value);
                }
            })
        }
    })
}