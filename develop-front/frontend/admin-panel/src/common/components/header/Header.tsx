import { Link } from "react-router-dom";
import "@/common/components/header/style.scss"
import { NavLink } from "react-router-dom";
import logo_admin from '/public/images/logo-admin.svg';

const Header = () => {
    return (
        <header className="header">
            <Link to="/dashboard" className="header__logo-link">
                <img src={logo_admin} alt='logo' />
            </Link>

            <nav className='header__nav'>
                <ul className="header__list">
                    <li>
                        <NavLink to='/dashboard' className="header__link">
                            Статистика
                        </NavLink>
                    </li>

                    <li>
                        <NavLink to='/applications' className="header__link">
                            Заявки
                        </NavLink>
                    </li>


                    <li>
                        <NavLink to='/settings' className="header__link">
                            Настройки
                        </NavLink>
                    </li>
                </ul>
            </nav>
        </header>
    )
};

export default Header;