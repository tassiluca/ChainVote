
interface navbarElement {
    id: string,
    class: string,
    href: string,
    positioning: string,
    roleRequired: string | undefined,
    text?: string,
    img?: {
        alt: string,
        class: string,
        src: string,
        width: string,
        height: string,
    },
}

const width: string = "30";
const height: string = "30";

function navbar(id?: string): navbarElement[] {
    return [
        {
            id: "home", class: "nav-link", href: "/", positioning: "left", roleRequired: 'everybody', text: "Home",
        },
        {
            id: "sign-in", class: "nav-link", href: "/sign-in", positioning: "right", roleRequired: 'unauthenticated',
            img: {
                alt: "Sign in", class: "nav-img", src: "/assets/sign-in.png", width: width, height: height
            },
        },
        {
            id: "sign-up", class: "nav-link", href: "/sign-up", positioning: "right", roleRequired: 'unauthenticated',
            img: {
                alt: "Sign up", class: "nav-img", src: "/assets/sign-up.png", width: width, height: height
            },
        },
        {
            id: "user-area", class: "nav-link", href: `/users/${id}`, positioning: "right", roleRequired: "authenticated",
            img: {
                alt: "User area", class: "nav-img", src: "/assets/user-area.png", width: width, height: height
            },
        },
        {
            id: "create-election", class: "nav-link", href: "/elections/create", positioning: "right", roleRequired: "admin",
            img: {
                alt: "User area", class: "nav-img", src: "/assets/create-election.png", width: width, height: height
            },
        },
        {
            id: "log-out", class: "nav-link", href: "/log-out", positioning: "right", roleRequired: "authenticated",
            img: {
                alt: "Log out", class: "nav-img", src: "/assets/log-out.png", width: width, height: height
            },
        },
    ]
}

export default navbar;
