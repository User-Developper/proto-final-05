document.addEventListener("DOMContentLoaded", () => {

    const links = document.querySelectorAll(".menu-link");

    const panels = document.querySelectorAll("[id$='panel']");

    links.forEach(link => {

        link.addEventListener("click", function (event) {

            event.preventDefault();

            links.forEach(item =>
                item.classList.remove("active")
            );

            this.classList.add("active");

            panels.forEach(panel =>
                panel.classList.add("hidden")
            );

            document
                .getElementById(this.dataset.panel)
                .classList.remove("hidden");

        });

    });

});