const executeUrlParameter = (key, value) => {
    window.location.href = URL_add_parameter(window.location.href, key, value);
};

function URL_add_parameter(url, param, value) {
    const hash = {};
    const parser = document.createElement('a');

    parser.href = url;

    const parameters = parser.search.split(/\?|&/);

    for (const element of parameters) {
        if (!element)
            continue;

        const ary = element.split('=');
        hash[ary[0]] = ary[1];
    }

    hash[param] = value;

    const list = [];
    Object.keys(hash).forEach(function (key) {
        list.push(key + '=' + hash[key]);
    });

    parser.search = '?' + list.join('&');
    return parser.href;
}