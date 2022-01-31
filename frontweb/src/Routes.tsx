import Admin from "pages/Admin";
import Catalog from "pages/Catalog";
import Home from "pages/Home";

import {
    BrowserRouter,
    Switch,
    Route,
  } from "react-router-dom";

function Routes() {
    return (
        <BrowserRouter>
            <Switch>
                <Route path="/" exact>
                    <Home />
                </Route>
                <Route path="/products" >
                    <Catalog />
                </Route>
                <Route path="/admin" >
                    <Admin />
                </Route>
            </Switch>   
        </BrowserRouter>
    );
  }
   
  export default Routes;