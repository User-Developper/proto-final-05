function toggleSidebar() {
    const sidebar = document.getElementById("sidebar");
    const overlay = document.getElementById("sidebar-overlay");

    sidebar.classList.toggle("open");
    overlay.classList.toggle("show");
}

window.toggleSidebar = toggleSidebar;

document.addEventListener("DOMContentLoaded", function () {

    const accountButtons = document.querySelectorAll(
        ".account-btn, .mobile-account-btn"
    );

    accountButtons.forEach(function (button) {

        button.addEventListener("click", function (event) {

            event.preventDefault();
            event.stopPropagation();

            const container = button.closest(".account-container");

            if (!container) {
                return;
            }

            const menu = container.querySelector(".account-menu");

            if (!menu) {
                return;
            }

            const alreadyOpen = menu.classList.contains("show");

            document.querySelectorAll(".account-menu.show")
                .forEach(function (otherMenu) {
                    otherMenu.classList.remove("show");
                });

            document.querySelectorAll(
                ".account-btn.active, .mobile-account-btn.active"
            ).forEach(function (otherButton) {
                otherButton.classList.remove("active");
            });

            if (!alreadyOpen) {
                menu.classList.add("show");
                button.classList.add("active");
            }

        });

    });

    document.addEventListener("click", function () {

        document.querySelectorAll(".account-menu.show")
            .forEach(function (menu) {
                menu.classList.remove("show");
            });

        document.querySelectorAll(
            ".account-btn.active, .mobile-account-btn.active"
        ).forEach(function (button) {
            button.classList.remove("active");
        });

    });

    document.querySelectorAll(".account-menu")
        .forEach(function (menu) {

            menu.addEventListener("click", function (event) {
                event.stopPropagation();
            });

        });

});